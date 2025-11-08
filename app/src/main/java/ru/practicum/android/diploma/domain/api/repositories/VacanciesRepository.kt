package ru.practicum.android.diploma.domain.api.repositories

import android.graphics.Region
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

interface VacanciesRepository {

    val totalFoundCount: Int
    suspend fun searchVacancies(
        query: String,
        page: Int = 1,
        pageSize: Int,
        filters: FilterIndustry
    ): Result<List<VacancyDetails>>

    suspend fun getVacancyDetails(vacancyId: String): Result<VacancyDetails>

    suspend fun getIndustries(): Result<List<VacancyDetails>>

    suspend fun getRegions(countryCode: String? = null): Result<List<Region>>

    suspend fun isNetworkAvailable(): Boolean
}
