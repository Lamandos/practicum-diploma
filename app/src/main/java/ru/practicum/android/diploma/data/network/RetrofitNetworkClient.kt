package ru.practicum.android.diploma.data.network

import ru.practicum.android.diploma.data.dto.Response
import ru.practicum.android.diploma.data.dto.ResponseError
import ru.practicum.android.diploma.data.dto.ResponseSuccess

class RetrofitNetworkClient(
    private val apiService: VacancySearchApiService
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        return try {
            when (dto) {
                is VacancySearchRequest -> {
                    val result = apiService.searchVacancies(dto.toQueryMap())
                    ResponseSuccess(result)
                }
                else -> ResponseError("Неизвестный тип запроса")
            }
        } catch (e: Exception) {
            ResponseError(e.message ?: "Неизвестная ошибка сети")
        }
    }
}
