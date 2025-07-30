package ru.steps

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send

object ConnectToServer {
    val client = HttpClient(CIO) {
        install(WebSockets)
    }

    suspend fun sendMessage() {
        client.webSocket(
            method = HttpMethod.Get,
            host = "192.168.100.2",
            port = 8888,
            path = "/server1"
        ) {
            send("Привет, сервер!")

            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        println("Получено: $text")
                    }

                    else -> {}
                }
            }
        }
    }
}
