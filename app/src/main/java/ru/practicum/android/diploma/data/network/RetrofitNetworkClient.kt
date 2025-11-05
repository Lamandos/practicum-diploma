package ru.practicum.android.diploma.data.network

import ru.practicum.android.diploma.data.dto.Response
import ru.practicum.android.diploma.data.dto.ResponseError
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.dto.vacancydetailsdto.VacancyDetailsDto
import ru.practicum.android.diploma.data.mappers.VacancyMapper
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
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

    override suspend fun getVacancyDetails(vacancyId: String): VacancyDetails? {
        return try {
            val apiResponse: VacancyDetailsDto = apiService.getVacancyDetails(vacancyId)
            VacancyMapper.mapToDomain(apiResponse)
        } catch (e: IOException) {
            null
        } catch (e: retrofit2.HttpException) {
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
