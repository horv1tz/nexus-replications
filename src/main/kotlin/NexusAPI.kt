package ru.cib

import okhttp3.OkHttpClient
import okhttp3.Request
import okio.sink
import java.io.File

class NexusAPI {
    val ENDPOINT = "https://packages.dvorfs.com"
    val NXTOKEN = "0.915381105509447"
    val client = OkHttpClient()

    // Получение списка всех репозиториев
    fun getAllRepositories(): String? {
        val request = Request.Builder()
            .url("${ENDPOINT}/service/rest/v1/repositories")
            .addHeader("accept", "application/json")
            .addHeader("NX-ANTI-CSRF-TOKEN", NXTOKEN)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return "Ошибка: ${response.code}"
            }

            return response.body?.string()
        }
    }

    // Получение всех артифактов в репозитории
    fun getAllComponentsInRepository(repository: String?): String? {
        val request = Request.Builder()
            .url("${ENDPOINT}/service/rest/v1/components?repository=${repository}")
            .addHeader("accept", "application/json")
            .addHeader("NX-ANTI-CSRF-TOKEN", NXTOKEN)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return "Ошибка: ${response.code}"
            }

            return response.body?.string()
        }
    }

    // Скачивание компонента
    private fun downloadComponent(path: String, outputFileName: String) {
        val request = Request.Builder()
            .url("${ENDPOINT}${path}")
            .addHeader("NX-ANTI-CSRF-TOKEN", NXTOKEN)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                println("Ошибка: ${response.code}")
                return
            }

            val file = File(outputFileName)
            file.outputStream().use { outputStream ->
                response.body?.byteStream()?.use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            println("Файл успешно загружен: ${file.absolutePath}")
        }
    }
}