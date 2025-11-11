package ru.practicum.android.diploma.data.repositories

import android.graphics.Region
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.mappers.VacancyMapper
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.VacancySearchRequest
import ru.practicum.android.diploma.data.network.VacancySearchResponse
import ru.practicum.android.diploma.domain.api.repositories.VacanciesRepository
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import java.io.IOException
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
        filters: VacancyFilters?,
    ): Result<List<VacancyDetails>> {
        return try {
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
                        val vacancies = VacancyMapper.mapToVacancyDetails(data.items)
                        Result.success(vacancies)
                    } else {
                        Result.failure(Exception(ERROR_INVALID_RESPONSE))
                    }
                }
                else -> {
                    Result.failure(Exception("$ERROR_NETWORK: $response"))
                }
            }
        } catch (e: UnknownHostException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: IllegalStateException) {
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
