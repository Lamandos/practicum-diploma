package ru.practicum.android.diploma.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.data.storage.FilterPreferences
import ru.practicum.android.diploma.domain.api.repositories.FilterRepository
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters

class FilterRepositoryImpl(
    private val filterPreferences: FilterPreferences
) : FilterRepository {

    override suspend fun getFilters(): VacancyFilters = withContext(Dispatchers.IO) {
        return@withContext filterPreferences.getFilters()
    }

    override suspend fun saveFilters(filters: VacancyFilters) = withContext(Dispatchers.IO) {
        filterPreferences.saveFilters(filters)
    }

    override suspend fun clearFilters() = withContext(Dispatchers.IO) {
        filterPreferences.clearFilters()
    }
}
