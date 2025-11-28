package ru.practicum.android.diploma.domain.api.usecases

import ru.practicum.android.diploma.domain.api.repositories.FilterRepository
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters

class FilterUseCase(
    private val filterRepository: FilterRepository
) {
    suspend fun getCurrentFilters(): VacancyFilters = filterRepository.getFilters()
    suspend fun saveFilters(filters: VacancyFilters) = filterRepository.saveFilters(filters)
}
