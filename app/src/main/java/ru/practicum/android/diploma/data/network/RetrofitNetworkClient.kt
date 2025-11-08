package ru.practicum.android.diploma.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.data.dto.Response
import ru.practicum.android.diploma.data.dto.ResponseError
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.dto.filterdto.FilterAreaDto
import ru.practicum.android.diploma.data.dto.vacancydetailsdto.VacancyDetailsDto
import ru.practicum.android.diploma.data.mappers.VacancyMapper
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private const val TAG = "RetrofitNetworkClient"

class RetrofitNetworkClient(
    private val apiService: VacancySearchApiService,
    private val areasService: AreasApiService
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
                else -> ResponseError(Throwable("Неизвестный тип запроса"))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Ошибка сети", e)
            ResponseError(Throwable("Ошибка сети: ${e.message}", e))
        } catch (e: retrofit2.HttpException) {
            Log.e(TAG, "Ошибка HTTP", e)
            ResponseError(Throwable("Сервер вернул ошибку: ${e.message()}", e))
        }
    }

    override suspend fun getVacancyDetails(vacancyId: String): VacancyDetails? {
        return try {
            val apiResponse: VacancyDetailsDto = apiService.getVacancyDetails(vacancyId)
            VacancyMapper.mapToDomain(apiResponse)
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "Таймаут подключения для vacancyId: $vacancyId", e)
            null
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Нет подключения к интернету для vacancyId: $vacancyId", e)
            null
        } catch (e: IOException) {
            Log.e(TAG, "Ошибка ввода-вывода для vacancyId: $vacancyId", e)
            null
        } catch (e: retrofit2.HttpException) {
            Log.e(TAG, "HTTP ошибка для vacancyId: $vacancyId", e)
            null
        }
    }
    override suspend fun getAreas(): Response = withContext(Dispatchers.IO) {
        try {
            val result: List<FilterAreaDto> = areasService.getAreas()
            ResponseSuccess(FilterAreaResponse(result))
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Нет интернета при запросе /areas", e)
            ResponseError(Throwable("Нет подключения к интернету"))
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "Таймаут при запросе /areas", e)
            ResponseError(Throwable("Превышено время ожидания сервера"))
        } catch (e: IOException) {
            Log.e(TAG, "Ошибка сети при запросе /areas", e)
            ResponseError(Throwable("Ошибка сети: ${e.message}"))
        } catch (e: retrofit2.HttpException) {
            Log.e(TAG, "HTTP ошибка при запросе /areas", e)
            ResponseError(Throwable("Ошибка сервера: ${e.message()}"))
        }
    }
    override suspend fun getIndustries(): Response {
        return try {
            val result = apiService.getIndustries()
            ResponseSuccess(result)
        } catch (e: Exception) {
            ResponseError(e)
        }
    }
}
