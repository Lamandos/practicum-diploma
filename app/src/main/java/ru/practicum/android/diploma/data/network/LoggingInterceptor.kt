// LoggingInterceptor.kt
package ru.practicum.android.diploma.data.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import java.nio.charset.StandardCharsets

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Логируем запрос
        Log.d("API_REQUEST", "URL: ${request.url}")
        Log.d("API_REQUEST", "Method: ${request.method}")

        val response = chain.proceed(request)

        // Логируем ответ
        Log.d("API_RESPONSE", "Status: ${response.code} ${response.message}")

        val responseBody = response.body
        val source = responseBody?.source()
        source?.request(Long.MAX_VALUE)
        val buffer = source?.buffer?.clone()

        val contentType = responseBody?.contentType()
        val charset = contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

        if (buffer != null && buffer.size > 0) {
            val jsonString = buffer.readString(charset)
            Log.d("API_RESPONSE_JSON", "Response JSON: $jsonString")

            // Особенно логируем часть с контактами
            if (jsonString.contains("contacts")) {
                val contactsStart = jsonString.indexOf("\"contacts\"")
                if (contactsStart != -1) {
                    val contactsEnd = findMatchingBracket(jsonString, contactsStart + 10)
                    if (contactsEnd != -1) {
                        val contactsJson = jsonString.substring(contactsStart, contactsEnd + 1)
                        Log.d("API_CONTACTS_DETAIL", "Contacts JSON: $contactsJson")
                    }
                }
            }
        }

        return response
    }

    private fun findMatchingBracket(json: String, startIndex: Int): Int {
        var count = 0
        for (i in startIndex until json.length) {
            when (json[i]) {
                '{', '[' -> count++
                '}', ']' -> {
                    count--
                    if (count == 0) return i
                }
            }
        }
        return -1
    }
}
