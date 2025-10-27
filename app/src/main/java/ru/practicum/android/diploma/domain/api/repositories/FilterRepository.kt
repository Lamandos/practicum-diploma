package ru.practicum.android.diploma.domain.api.repositories

import ru.practicum.android.diploma.domain.models.filterModels.VacancyFilters
import ru.practicum.android.diploma.domain.models.vacancy.Country

interface FilterRepository {
    // сохранение фильтров
    suspend fun saveFilters(filters: VacancyFilters)

    // получение сохраненных фильтров
    suspend fun getFilters(): VacancyFilters?

    // сброс фильтров
    suspend fun clearFilters()

    // проверка, есть ли активные фильтры
    suspend fun hasActiveFilters(): Boolean

    // сохранение выбранной страны
    suspend fun saveSelectedCountry(country: Country?)

    // солучение выбранной страны
    suspend fun getSelectedCountry(): Country?
}
