package ru.practicum.android.diploma.data.network

import ru.practicum.android.diploma.data.dto.Response
import ru.practicum.android.diploma.data.dto.ResponseError
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import java.io.IOException

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
                is VacancyDetailsRequest -> {
                    val result = apiService.getVacancyDetails(dto.vacancyId)
                    ResponseSuccess(result)
                }
                else -> ResponseError("Неизвестный тип запроса")
            }
        } catch (e: IOException) {
            ResponseError("Ошибка сети: ${e.message}")
        } catch (e: retrofit2.HttpException) {
            ResponseError("Сервер вернул ошибку: ${e.message()}")
        }
    }
}
