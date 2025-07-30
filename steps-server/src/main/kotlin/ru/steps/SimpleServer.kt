package ru.steps

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.Inet4Address
import java.net.NetworkInterface

fun main() {
//    OBSServer.modifyStreamPashka(false)
    val localIp = getLocalIP()
    embeddedServer(Netty, 8888, localIp) {
        install(WebSockets)
        routing {
            webSocket("/server1") {
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    println(receivedText)
                    send("Привет, сервер!")
                }
            }
        }
    }.start(wait = true)
}

fun sendMessage() {
    val client = HttpClient(CIO) {
        install(io.ktor.client.plugins.websocket.WebSockets)
    }

    GlobalScope.launch {
        client.webSocket("ws://localhost:8888/server1") {
            while (true) {
                send("Привет от Ktor!")
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