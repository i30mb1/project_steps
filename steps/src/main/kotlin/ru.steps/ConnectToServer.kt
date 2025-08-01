package ru.steps

import java.io.OutputStreamWriter
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import kotlin.collections.iterator
import kotlinx.coroutines.flow.MutableSharedFlow

object Server {

    val state = MutableSharedFlow<String>()

   suspend fun createServer() {
        val ip = getLocalIP()
        state.emit(ip)
        val server = ServerSocket(8888, 0, InetAddress.getByName(ip))
        val socket = server.accept()

        val writer = OutputStreamWriter(socket.getOutputStream())
        writer.write("Привет!\n")
        writer.flush()

        socket.close()
        server.close()
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
}
