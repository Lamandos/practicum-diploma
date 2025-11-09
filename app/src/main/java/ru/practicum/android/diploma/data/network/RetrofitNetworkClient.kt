package ru.practicum.android.diploma.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.data.dto.Response
import ru.practicum.android.diploma.data.dto.ResponseError
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.dto.filterdto.FilterAreaDto
import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto
import ru.practicum.android.diploma.data.dto.vacancydetailsdto.VacancyDetailsDto
import ru.practicum.android.diploma.data.mappers.VacancyMapper
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private const val TAG = "RetrofitNetworkClient"

class RetrofitNetworkClient(
    private val apiService: VacancySearchApiService,
    private val areasService: AreasApiService,
    private val industriesApiService: IndustriesApiService
) : NetworkClient {

    companion object {
        private const val ERROR_NO_INTERNET = "Нет подключения к интернету"
        private const val ERROR_SERVER_TIMEOUT = "Превышено время ожидания сервера"
        private const val ERROR_NETWORK = "Ошибка сети"
        private const val ERROR_SERVER = "Ошибка сервера"
        private const val ERROR_UNKNOWN_REQUEST = "Неизвестный тип запроса"
        private const val ERROR_NETWORK_PREFIX = "$ERROR_NETWORK: "
        private const val ERROR_SERVER_PREFIX = "$ERROR_SERVER: "
    }

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
                else -> ResponseError(Throwable(ERROR_UNKNOWN_REQUEST))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Ошибка сети", e)
            ResponseError(Throwable(ERROR_NETWORK_PREFIX + e.message, e))
        } catch (e: retrofit2.HttpException) {
            Log.e(TAG, "Ошибка HTTP", e)
            ResponseError(Throwable(ERROR_SERVER_PREFIX + e.message(), e))
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
            logAndReturnError("Нет интернета при запросе /areas", e, ERROR_NO_INTERNET)
        } catch (e: SocketTimeoutException) {
            logAndReturnError("Таймаут при запросе /areas", e, ERROR_SERVER_TIMEOUT)
        } catch (e: IOException) {
            logAndReturnError("Ошибка сети при запросе /areas", e, ERROR_NETWORK_PREFIX + e.message)
        } catch (e: retrofit2.HttpException) {
            logAndReturnError("HTTP ошибка при запросе /areas", e, ERROR_SERVER_PREFIX + e.message())
        }
    }

    override suspend fun getIndustries(dto: Any): Response {
        return try {
            when (dto) {
                is FilterIndustryRequest -> {
                    val apiResponse: List<FilterIndustryDto> = industriesApiService.getIndustries()
                    val responseWrapper = FilterIndustryResponse(apiResponse)
                    ResponseSuccess(responseWrapper)
                }
                else -> {
                    ResponseError(Throwable("Неизвестный тип запроса"))
                }
            }
        } catch (e: UnknownHostException) {
            logAndReturnError("Нет интернета при запросе /industries", e, ERROR_NO_INTERNET)
        } catch (e: SocketTimeoutException) {
            logAndReturnError("Таймаут при запросе /industries", e, ERROR_SERVER_TIMEOUT)
        } catch (e: IOException) {
            logAndReturnError("Ошибка сети при запросе /industries", e, ERROR_NETWORK_PREFIX + e.message)
        } catch (e: retrofit2.HttpException) {
            logAndReturnError("HTTP ошибка при запросе /industries", e, ERROR_SERVER_PREFIX + e.message())
        }
    }

    private fun logAndReturnError(logMessage: String, exception: Exception, errorMessage: String): ResponseError {
        Log.e(TAG, logMessage, exception)
        return ResponseError(Throwable(errorMessage, exception))
    }
}
