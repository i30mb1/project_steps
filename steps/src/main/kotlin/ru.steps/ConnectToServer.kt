package ru.steps

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.ServerSocket
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import kotlin.experimental.xor
import kotlin.time.Duration.Companion.seconds
import android.util.Base64
import kotlinx.coroutines.flow.MutableSharedFlow

object Server {

    val state = MutableSharedFlow<String>()
    private lateinit var server: ServerSocket

    suspend fun createServer() {
        val ip = getLocalIP()
        state.emit(ip)
        server = ServerSocket(50077)
        while (true) {
            val socket = server.accept()

            val inputStream = socket.getInputStream()
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val outputStream = socket.getOutputStream()
            val writer = PrintWriter(outputStream)

            val headers = readHeaders(bufferedReader)
            handshake(writer, headers)

            while (true) {
                val receivedFrame = readSocketFrame(inputStream)
                when (receivedFrame) {
                    FrameType.Close -> return
                    is FrameType.Text -> {
                        sendSocketFrame(outputStream, "Hello")
                    }
                    null -> null
                }
            }
        }
    }

    private fun readSocketFrame(input: InputStream): FrameType? {
        val opcode = input.read() and OP_CODE_MASK
        val payloadLength = input.read() and PAYLOAD_LENGTH_MASK
        val maskKey = ByteArray(4).apply { input.read(this) }
        val payload = ByteArray(payloadLength).apply { input.read(this) }
        for (i in payload.indices) {
            payload[i] = payload[i] xor maskKey[i % 4]
        }
        return when (opcode) {
            OP_CODE_TEXT -> FrameType.Text(String(payload, StandardCharsets.UTF_8))
            OP_CODE_CLOSE -> FrameType.Close
            else -> null
        }
    }

    private fun sendSocketFrame(output: OutputStream, text: String) {
        val utf8Bytes = text.toByteArray(StandardCharsets.UTF_8)
        output.write(BASIC_SENDING_FRAME)
        output.write(utf8Bytes.size)
        output.write(utf8Bytes)
        output.flush()
    }

    private fun handshake(writer: PrintWriter, headers: Map<String, String>) {
        val upgrade = headers[UPGRADE] ?: error("no handshake:upgrade")
        val connection = headers[CONNECTION] ?: error("no handshake:connection")
        val key = headers[SEC_WEBSOCKET_KEY] ?: error("no handshake:key")
        val encodedKey = calculateWebSocketAccept(key)
//        val requestedProtocols = headers[SEC_WEBSOCKET_PROTOCOL]?.split(",")!!
        writer.println(WEB_SOCKET_HEADER)
        writer.println("$UPGRADE:$upgrade")
        writer.println("$CONNECTION:$connection")
        writer.println("$SEC_WEBSOCKET_ACCEPT_KEY:$encodedKey")
//        writer.println("${SEC_WEBSOCKET_PROTOCOL}:${requestedProtocols.first()}")
        writer.println()
        writer.flush()
    }

    private fun calculateWebSocketAccept(clientWebSocketKey: String): String {
        val concatenated = clientWebSocketKey + WEB_SOCKET_GUID

        val sha1 = MessageDigest.getInstance("SHA-1")
        val hashBytes = sha1.digest(concatenated.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
    }

    private fun readHeaders(reader: BufferedReader): Map<String, String> = buildMap {
        var line: String? = reader.readLine()
        while (!line.isNullOrEmpty()) {
            val headerParts = line.split(":", limit = 2)
            if (headerParts.size == 2) {
                val headerName = headerParts[0].trim()
                val headerValue = headerParts[1].trim()
                if (headerName.isNotBlank() && headerValue.isNotBlank()) {
                    put(headerName, headerValue)
                }
            }
            line = reader.readLine()
        }
    }

    fun getLocalIP(): String {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        for (iface in interfaces) {
            if (!iface.isUp || iface.isLoopback) continue
            for (addr in iface.inetAddresses) {
                if (addr is Inet4Address && !addr.isLoopbackAddress) {
                    return addr.hostAddress
                }
            }
        }
        error("Not found local ip")
    }

    private val minTimeoutForSocket = 1.seconds.inWholeMilliseconds
    private const val UPGRADE = "Upgrade"
    private const val CONNECTION = "Connection"
    private const val SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key"
    private const val SEC_WEBSOCKET_ACCEPT_KEY = "Sec-WebSocket-Accept"
    private const val SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol"
    private const val WEB_SOCKET_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
    private const val WEB_SOCKET_HEADER = "HTTP/1.1 101 Switching Protocols"
    private const val OP_CODE_TEXT = 1
    private const val OP_CODE_CLOSE = 8
    private const val BASIC_SENDING_FRAME = 0b10000001
    private const val OP_CODE_MASK = 0b00001111
    private const val PAYLOAD_LENGTH_MASK = 0b01111111
    private const val REQUEST_IDENTIFIER = "requestMarketingID"
    private const val SUB_PROTOCOL_V1 = "v1"
    const val IDENTIFIER_TYPE = "marketingID"
    const val ERROR_TYPE = "typeError"
}

internal sealed class FrameType {
    class Text(val text: String) : FrameType()
    object Close : FrameType()
}
