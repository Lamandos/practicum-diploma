package ru.practicum.android.diploma.domain.interactors

import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

interface SearchVacanciesInteractor {
    val totalFoundCount: Int

    suspend fun searchVacancies(
        query: String,
        page: Int = 1,
        pageSize: Int = 20,
        filters: FilterIndustry = FilterIndustry(0, "")
    ): Result<List<VacancyDetails>>

}
