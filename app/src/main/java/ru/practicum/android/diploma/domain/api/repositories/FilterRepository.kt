package ru.practicum.android.diploma.domain.api.repositories

import android.graphics.Region
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry

interface FilterRepository {
    // сохранение фильтров
    suspend fun saveFilters(filters: FilterIndustry)

    // получение сохраненных фильтров
    suspend fun getFilters(): FilterIndustry

    // сброс фильтров
    suspend fun clearFilters()

    // проверка, есть ли активные фильтры
    suspend fun hasActiveFilters(): Boolean

    // сохранение выбранной страны
    suspend fun saveSelectedCountry(country: Region)

    // получение выбранной страны
    suspend fun getSelectedCountry(): Region
}
