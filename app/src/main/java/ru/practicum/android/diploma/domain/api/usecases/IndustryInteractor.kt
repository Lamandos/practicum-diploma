package ru.practicum.android.diploma.domain.api.usecases

import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto

interface IndustryInteractor {
    suspend fun getAllIndustries(): List<FilterIndustryDto>?
    suspend fun searchIndustries(query: String): List<FilterIndustryDto>?
}
