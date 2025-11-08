package ru.practicum.android.diploma.domain.interactor

import ru.practicum.android.diploma.domain.api.repositories.VacanciesRepository
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

class VacancyInteractor(private val repository: VacanciesRepository) {
    suspend fun getVacancyDetails(id: String): Result<VacancyDetails> =
        repository.getVacancyDetails(id)

    suspend fun searchVacancies(
        query: String,
        page: Int = 1,
        pageSize: Int = 20,
        filters: FilterIndustry = FilterIndustry("", "")
    ): Result<List<VacancyDetails>> =
        repository.searchVacancies(query, page, pageSize, filters)
}
