package ru.practicum.android.diploma.domain.api.usecases

import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto
import ru.practicum.android.diploma.domain.api.repositories.IndustryRepository

class IndustryInteractorImpl(
    private val industriesRepository: IndustryRepository
) : IndustryInteractor {

    override suspend fun getAllIndustries(): List<FilterIndustryDto>? {
        return industriesRepository.getAllIndustries()
    }

    override suspend fun searchIndustries(query: String): List<FilterIndustryDto>? {
        val allIndustries = industriesRepository.getAllIndustries()
        return allIndustries?.filter { industry ->
            industry.name.contains(query, ignoreCase = true)
        }
    }
}
