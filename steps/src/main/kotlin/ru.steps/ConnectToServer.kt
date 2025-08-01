package ru.steps

import java.io.OutputStreamWriter
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import kotlin.collections.iterator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

object Server {

    val state = MutableSharedFlow<String>()

    suspend fun createServer() {
        val ip = getLocalIP()
        state.emit(ip)
        val server = ServerSocket(8888, 0, InetAddress.getByName(ip))
        while (true) {
            val socket = server.accept()
            GlobalScope.launch {
                val writer = OutputStreamWriter(socket.getOutputStream())
                while (true) {
                    delay(1000)
                    writer.write("Привет!\n")
                    writer.flush()
                }
            }
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
}
