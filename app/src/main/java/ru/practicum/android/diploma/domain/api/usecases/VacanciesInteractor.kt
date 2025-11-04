package ru.practicum.android.diploma.domain.api.usecases

import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

interface VacanciesInteractor {

    suspend fun searchVacancies(
        query: String,
        page: Int = 1,
        pageSize: Int = 20,
        filters: FilterIndustry = FilterIndustry("", "")
    ): Result<List<VacancyDetails>>
}
