package ru.practicum.android.diploma.data.repository

import android.graphics.Region
import ru.practicum.android.diploma.data.dto.ResponseError
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.VacancySearchRequest
import ru.practicum.android.diploma.domain.api.repositories.VacanciesRepository
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.domain.models.vacancydetails.Salary
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

class VacanciesRepositoryImpl(
    private val networkClient: NetworkClient,
) : VacanciesRepository {

    override suspend fun searchVacancies(
        query: String,
        page: Int,
        pageSize: Int,
        filters: FilterIndustry,
    ): Result<List<VacancyDetails>> {
        return try {
            val response = networkClient.doRequest(
                VacancySearchRequest(text = query, page = page, perPage = pageSize)
            )
            when (response) {
                is ResponseSuccess<*> -> {
                    val data = response.data as ru.practicum.android.diploma.data.network.VacancySearchResponse
                    val vacancies = data.items.map { item ->
                        VacancyDetails(
                            id = item.id,
                            name = item.name,
                            description = "",
                            salary = item.salary?.let { s ->
                                Salary(
                                    from = s.from,
                                    to = s.to,
                                    currency = s.currency
                                )
                            },
                            address = null,
                            experience = "",
                            schedule = "",
                            employment = "",
                            employer = item.employer.name,
                            contacts = null,
                            area = item.area.name,
                            skills = emptyList(),
                            url = "",
                            industry = "",
                            publishedAt = null
                        )
                    }
                    Result.success(vacancies)
                }

                is ResponseError -> {
                    Result.failure(Exception(response.message))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getVacancyDetails(vacancyId: String): Result<VacancyDetails> {
        return Result.failure(Exception("Not implemented"))
    }

    override suspend fun getIndustries(): Result<List<VacancyDetails>> {
        return Result.failure(Exception("Not implemented"))
    }

    override suspend fun getRegions(countryCode: String?): Result<List<Region>> {
        return Result.failure(Exception("Not implemented"))
    }

    override suspend fun isNetworkAvailable(): Boolean {
        return true
    }
}
