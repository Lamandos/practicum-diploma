package ru.practicum.android.diploma.domain.api.usecases

import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters

interface FilterInteractor {
    suspend fun saveFilters(filters: VacancyFilters)
    suspend fun getFilters(): VacancyFilters
    suspend fun clearFilters()
}
