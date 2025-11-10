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

class SearchVacanciesRepositoryImpl(
    private val networkClient: NetworkClient,
) : VacanciesRepository {

    private var totalFound: Int = 0

    override val totalFoundCount: Int
        get() = totalFound

    override suspend fun searchVacancies(
        query: String,
        page: Int,
        pageSize: Int,
        filters: VacancyFilters?
    ): Result<List<VacancyDetails>> {
        return try {
            Log.d("SearchRepository", "Search started: query='$query', page=$page, filters=$filters")

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

                        Log.d("SearchRepository",
                            "Search successful: found=${data.found}, pages=${data.pages}, " +
                                "currentPage=${data.page}, items=${data.items.size}"
                        )

                        val vacancies = VacancyMapper.mapToVacancyDetails(data.items)
                        Result.success(vacancies)
                    } else {
                        Log.e("SearchRepository", "Invalid response data type")
                        Result.failure(Exception("Invalid response data type"))
                    }
                }
                else -> {
                    Log.e("SearchRepository", "Network error: $response")
                    Result.failure(Exception("Network error: $response"))
                }
            }
        } catch (e: Exception) {
            Log.e("SearchRepository", "Search error: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getVacancyDetails(vacancyId: String): Result<VacancyDetails> {
        return networkClient.getVacancyDetails(vacancyId)?.let {
            Result.success(it)
        } ?: Result.failure(Exception("Не удалось получить данные вакансии"))
    }

    override suspend fun getIndustries(): Result<List<VacancyDetails>> =
        Result.failure(Exception("Not implemented"))

    override suspend fun getRegions(countryCode: String?): Result<List<Region>> =
        Result.failure(Exception("Not implemented"))

    override suspend fun isNetworkAvailable(): Boolean = true
}
