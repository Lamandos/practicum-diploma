package ru.practicum.android.diploma.domain.interactors.impl

import ru.practicum.android.diploma.domain.models.filtermodels.Region
import ru.practicum.android.diploma.domain.models.vacancy.Country

interface RegionsInteractor {
    suspend fun getRegions(country: Country): List<Region>
    suspend fun getAllRegions(): List<Region>
}
