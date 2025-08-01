package ru.steps

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable

/**
 * @author e.shuvagin
 */
object HttpServer {

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun sendSteps(steps: Int) {
        try {
            val response = client.post("https://neighborly-rat-821.convex.site/api/steps") {
                contentType(ContentType.Application.Json)
                setBody(response(steps))
            }
            println("Ответ сервера: ${response.status}")
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
        }
    }
}

fun response(steps: Int): ApiResponse {
    return ApiResponse(Data(listOf(Metric(listOf(MetricData(steps))))))
}

@Serializable
data class ApiResponse(
    val data: Data
)

@Serializable
data class Data(
    val metrics: List<Metric>
)

@Serializable
data class Metric(
    val data: List<MetricData>
)

@Serializable
data class MetricData(
    val qty: Int
)
