package ru.practicum.android.diploma.domain.interactors.impl

import ru.practicum.android.diploma.domain.api.repositories.IndustryRepository
import ru.practicum.android.diploma.domain.interactors.IndustryInteractor
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry

class IndustryInteractorImpl(
    private val industriesRepository: IndustryRepository
) : IndustryInteractor {

    override suspend fun getAllIndustries(): List<FilterIndustry>? {
        return industriesRepository.getAllIndustries()
    }

    override suspend fun searchIndustries(query: String): List<FilterIndustry>? {
        val allIndustries = industriesRepository.getAllIndustries()
        return allIndustries?.filter { industry ->
            industry.name.contains(query, ignoreCase = true)
        }
    }
}
