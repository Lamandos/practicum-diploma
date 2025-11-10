package ru.practicum.android.diploma.data.repositories

import android.graphics.Region
import android.util.Log
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.mappers.VacancyMapper
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.VacancySearchRequest
import ru.practicum.android.diploma.data.network.VacancySearchResponse
import ru.practicum.android.diploma.domain.api.repositories.VacanciesRepository
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SearchVacanciesRepositoryImpl(
    private val networkClient: NetworkClient,
) : VacanciesRepository {

    private var totalFound: Int = 0

    override val totalFoundCount: Int
        get() = totalFound

    companion object {
        private const val TAG = "SearchRepository"
        private const val ERROR_INVALID_RESPONSE = "Invalid response data type"
        private const val ERROR_NETWORK = "Network error"
        private const val ERROR_VACANCY_DETAILS = "Не удалось получить данные вакансии"
    }

    override suspend fun searchVacancies(
        query: String,
        page: Int,
        pageSize: Int,
        filters: VacancyFilters?
    ): Result<List<VacancyDetails>> {
        return try {
            Log.d(TAG, "Search started: query='$query', page=$page, filters=$filters")

            val searchRequest = VacancySearchRequest(
                text = query,
                page = page,
                perPage = pageSize,
                area = filters?.region?.id,
                industry = filters?.industry?.id,
                salary = filters?.salary,
                onlyWithSalary = filters?.hideWithoutSalary
            )

            val response = networkClient.doRequest(searchRequest)

            when (response) {
                is ResponseSuccess<*> -> {
                    val data = response.data as? VacancySearchResponse
                    if (data != null) {
                        totalFound = data.found

                        Log.d(
                            TAG,
                            "Search successful: found=${data.found}, pages=${data.pages}, " +
                                "currentPage=${data.page}, items=${data.items.size}"
                        )

                        val vacancies = VacancyMapper.mapToVacancyDetails(data.items)
                        Result.success(vacancies)
                    } else {
                        Log.e(TAG, ERROR_INVALID_RESPONSE)
                        Result.failure(Exception(ERROR_INVALID_RESPONSE))
                    }
                }
                else -> {
                    Log.e(TAG, "$ERROR_NETWORK: $response")
                    Result.failure(Exception("$ERROR_NETWORK: $response"))
                }
            }
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "Search timeout error: ${e.message}", e)
            Result.failure(e)
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Search network error: ${e.message}", e)
            Result.failure(e)
        } catch (e: IOException) {
            Log.e(TAG, "Search IO error: ${e.message}", e)
            Result.failure(e)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Search state error: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getVacancyDetails(vacancyId: String): Result<VacancyDetails> {
        return networkClient.getVacancyDetails(vacancyId)?.let {
            Result.success(it)
        } ?: Result.failure(Exception(ERROR_VACANCY_DETAILS))
    }

    override suspend fun getIndustries(): Result<List<VacancyDetails>> =
        Result.failure(Exception("Not implemented"))

    override suspend fun getRegions(countryCode: String?): Result<List<Region>> =
        Result.failure(Exception("Not implemented"))

    override suspend fun isNetworkAvailable(): Boolean = true
}
