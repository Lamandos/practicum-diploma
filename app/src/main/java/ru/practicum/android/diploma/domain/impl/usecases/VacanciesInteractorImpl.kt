package ru.practicum.android.diploma.domain.impl.usecases

import ru.practicum.android.diploma.domain.api.repositories.VacanciesRepository
import ru.practicum.android.diploma.domain.api.usecases.VacanciesInteractor
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

class VacanciesInteractorImpl(
    private val repository: VacanciesRepository,
) : VacanciesInteractor {

    override suspend fun searchVacancies(
        query: String,
        page: Int,
        pageSize: Int,
        filters: FilterIndustry
    ): Result<List<VacancyDetails>> {
        return repository.searchVacancies(query, page, pageSize, filters)
    }
}
