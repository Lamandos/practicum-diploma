package ru.practicum.android.diploma.data.repository

import android.graphics.Region
import ru.practicum.android.diploma.data.dto.ResponseError
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.mappers.VacancyMapper
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.VacancySearchRequest
import ru.practicum.android.diploma.data.network.VacancySearchResponse
import ru.practicum.android.diploma.domain.api.repositories.VacanciesRepository
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
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
        val response = networkClient.doRequest(
            VacancySearchRequest(text = query, page = page, perPage = pageSize)
        )
        return when (response) {
            is ResponseSuccess<*> -> {
                val data = response.data as VacancySearchResponse
                val vacancies = VacancyMapper.mapToVacancyDetails(data.items)
                Result.success(vacancies)
            }

            is ResponseError -> {
                Result.failure(Exception(response.message))
            }
        }
    }

    override suspend fun getVacancyDetails(vacancyId: String): Result<VacancyDetails> {
        return Result.failure(Exception(NOT_IMPLEMENTED_MSG))
    }

    override suspend fun getIndustries(): Result<List<VacancyDetails>> {
        return Result.failure(Exception(NOT_IMPLEMENTED_MSG))
    }

    override suspend fun getRegions(countryCode: String?): Result<List<Region>> {
        return Result.failure(Exception(NOT_IMPLEMENTED_MSG))
    }

    override suspend fun isNetworkAvailable(): Boolean {
        return true
    }

    companion object {
        private const val NOT_IMPLEMENTED_MSG = "Not implemented"
    }
}
