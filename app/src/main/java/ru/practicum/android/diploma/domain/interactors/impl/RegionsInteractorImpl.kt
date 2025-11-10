package ru.practicum.android.diploma.domain.interactors.impl

import ru.practicum.android.diploma.domain.models.filtermodels.Region
import ru.practicum.android.diploma.domain.models.vacancy.Country

class RegionsInteractorImpl(private val repository: RegionsRepository) : RegionsInteractor {
    override suspend fun getRegions(country: Country): List<Region> =
        repository.getRegionsByCountry(country.id)

    override suspend fun getAllRegions(): List<Region> =
        repository.getAllRegions()
}
