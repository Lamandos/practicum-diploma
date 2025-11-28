package ru.practicum.android.diploma.domain.api.repositories

import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry

interface IndustryRepository {
    suspend fun getAllIndustries(): List<FilterIndustry>?
}
