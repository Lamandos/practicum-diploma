package ru.practicum.android.diploma.data.repositories

import ru.practicum.android.diploma.domain.api.repositories.FilterRepository
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters

class FilterRepositoryImpl : FilterRepository {

    private var currentFilters = VacancyFilters()

    override suspend fun getFilters(): VacancyFilters {
        return currentFilters
    }

    override suspend fun saveFilters(filters: VacancyFilters) {
        currentFilters = filters
    }

    fun setSelectedIndustry(industry: ru.practicum.android.diploma.domain.models.filtermodels.Industry?) {
        currentFilters = currentFilters.copy(industry = industry)
    }
}
