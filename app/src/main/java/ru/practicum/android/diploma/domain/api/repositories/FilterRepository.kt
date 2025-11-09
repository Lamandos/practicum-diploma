package ru.practicum.android.diploma.domain.api.repositories

import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters

interface FilterRepository {
    suspend fun getFilters(): VacancyFilters
    suspend fun saveFilters(filters: VacancyFilters)
}
