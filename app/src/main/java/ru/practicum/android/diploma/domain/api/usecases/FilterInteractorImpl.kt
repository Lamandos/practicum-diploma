package ru.practicum.android.diploma.domain.api.usecases

import ru.practicum.android.diploma.domain.api.repositories.FilterRepository
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters

class FilterInteractorImpl(
    private val filterRepository: FilterRepository
) : FilterInteractor {

    override suspend fun saveFilters(filters: VacancyFilters) {
        filterRepository.saveFilters(filters)
    }

    override suspend fun getFilters(): VacancyFilters {
        return filterRepository.getFilters()
    }

    override suspend fun clearFilters() {
        filterRepository.clearFilters()
    }
}
