package ru.practicum.android.diploma.data.repositories

import ru.practicum.android.diploma.data.dto.filterdto.FilterAreaDto
import ru.practicum.android.diploma.domain.interactors.impl.RegionsRepository
import ru.practicum.android.diploma.domain.models.filtermodels.Region
import ru.practicum.android.diploma.domain.models.vacancy.Country
import java.io.IOException

class RegionsRepositoryImpl(
    private val areasRepository: AreasRepository
) : RegionsRepository {

    override suspend fun getAllRegions(): List<Region> {
        val allAreas = areasRepository.getAllAreas()
            ?: throw IOException("Failed to load areas")
        return allAreas.flatMap { area ->
            collectAreaWithCountry(area)
        }
    }

    override suspend fun getRegionsByCountry(countryId: Int): List<Region> {
        val allAreas = areasRepository.getAllAreas()
            ?: throw IOException("Failed to load areas")
        val countryDto = allAreas.firstOrNull { it.id == countryId }
            ?: return emptyList()
        val country = Country(id = countryDto.id, name = countryDto.name)
        return collectRegions(countryDto, country)
    }

    private fun collectAreaWithCountry(area: FilterAreaDto): List<Region> {
        val country = if (area.parentId == null || area.parentId == 0) {
            Country(area.id, area.name)
        } else {
            null
        }
        return collectRegions(area, country)
    }

    private fun collectRegions(area: FilterAreaDto, country: Country?): List<Region> {
        if (area.areas.isEmpty()) {
            return listOf(Region(area.id, area.name, country))
        }

        val currentCountry = if (area.parentId == null || area.parentId == 0) {
            Country(area.id, area.name)
        } else {
            country
        }

        return area.areas.flatMap { collectRegions(it, currentCountry) }
    }
}
