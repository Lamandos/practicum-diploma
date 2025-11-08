package ru.practicum.android.diploma.domain.interactors.impl

import ru.practicum.android.diploma.domain.api.repositories.VacanciesRepository
import ru.practicum.android.diploma.domain.interactors.SearchVacanciesInteractor
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

class SearchVacanciesInteractorImpl(
    private val repository: VacanciesRepository,
) : SearchVacanciesInteractor {

    override val totalFoundCount: Int
        get() = repository.totalFoundCount

    override suspend fun searchVacancies(
        query: String,
        page: Int,
        pageSize: Int,
        filters: FilterIndustry,
    ): Result<List<VacancyDetails>> {
        return repository.searchVacancies(query, page, pageSize, filters)
    }
}
