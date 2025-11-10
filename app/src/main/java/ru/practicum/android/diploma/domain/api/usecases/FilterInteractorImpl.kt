package ru.practicum.android.diploma.domain.api.usecases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.data.storage.FilterPreferences
import ru.practicum.android.diploma.domain.api.repositories.FilterRepository
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters

class FilterInteractorImpl(
    private val filterPreferences: FilterPreferences
) : FilterInteractor {

    override suspend fun saveFilters(filters: VacancyFilters) {
        withContext(Dispatchers.IO) {
            filterPreferences.saveFilters(filters)
        }
    }

    override suspend fun getFilters(): VacancyFilters {
        return withContext(Dispatchers.IO) {
            filterPreferences.getFilters()
        }
    }

    override suspend fun clearFilters() {
        withContext(Dispatchers.IO) {
            filterPreferences.clearFilters()
        }
    }
}
