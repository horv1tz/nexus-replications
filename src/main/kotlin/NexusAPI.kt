package com.dvorfs

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import io.github.cdimascio.dotenv.dotenv

class NexusAPI {
    private val nexusdotenv = dotenv()

    private val endPoint = nexusdotenv["ENDPOINT"]
    private val nxToken = nexusdotenv["NXTOKEN"]
    private val client = OkHttpClient()

    // Получение списка всех репозиториев
    fun getAllRepositories(): String? {
        val request = Request.Builder()
            .url("${endPoint}/service/rest/v1/repositories")
            .addHeader("accept", "application/json")
            .addHeader("NX-ANTI-CSRF-TOKEN", nxToken)
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
            .url("${endPoint}/service/rest/v1/components?repository=${repository}")
            .addHeader("accept", "application/json")
            .addHeader("NX-ANTI-CSRF-TOKEN", nxToken)
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
            .url("${endPoint}${path}")
            .addHeader("NX-ANTI-CSRF-TOKEN", nxToken)
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