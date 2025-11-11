package ru.practicum.android.diploma.domain.interactors.impl

import ru.practicum.android.diploma.domain.models.filtermodels.Region

interface RegionsRepository {
    suspend fun getRegionsByCountry(countryId: Int): List<Region>
    suspend fun getAllRegions(): List<Region>
}
