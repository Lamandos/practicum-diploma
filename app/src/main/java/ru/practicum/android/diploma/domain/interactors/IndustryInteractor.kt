package ru.practicum.android.diploma.domain.interactors

import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry

interface IndustryInteractor {
    suspend fun getAllIndustries(): List<FilterIndustry>?
    suspend fun searchIndustries(query: String): List<FilterIndustry>?
}
