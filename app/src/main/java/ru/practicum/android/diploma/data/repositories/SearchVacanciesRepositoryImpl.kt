package ru.practicum.android.diploma.data.repositories

import android.graphics.Region
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.mappers.VacancyMapper
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.VacancySearchRequest
import ru.practicum.android.diploma.data.network.VacancySearchResponse
import ru.practicum.android.diploma.domain.api.repositories.VacanciesRepository
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
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
        filters: FilterIndustry,
    ): Result<List<VacancyDetails>> {
        return try {
            val response = networkClient.doRequest(
                VacancySearchRequest(
                    text = query,
                    page = page,
                    perPage = pageSize
                )
            )
            if (response is ResponseSuccess<*>) {
                val data = response.data as VacancySearchResponse
                totalFound = data.found
                val vacancies = VacancyMapper.mapToVacancyDetails(data.items)
                Result.success(vacancies)
            } else {
                Result.failure(Exception("Network error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getVacancyDetails(vacancyId: String): Result<VacancyDetails> =
        networkClient.getVacancyDetails(vacancyId)?.let { Result.success(it) }
            ?: Result.failure(Exception("Не удалось получить данные вакансии"))

    override suspend fun getIndustries(): Result<List<VacancyDetails>> =
        Result.failure(Exception("Not implemented"))

    override suspend fun getRegions(countryCode: String?): Result<List<Region>> =
        Result.failure(Exception("Not implemented"))

    override suspend fun isNetworkAvailable(): Boolean = true
}
