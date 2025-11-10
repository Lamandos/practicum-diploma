package ru.practicum.android.diploma.domain.api.repositories

import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto

interface IndustryRepository {
    suspend fun getAllIndustries(): List<FilterIndustryDto>?
}
