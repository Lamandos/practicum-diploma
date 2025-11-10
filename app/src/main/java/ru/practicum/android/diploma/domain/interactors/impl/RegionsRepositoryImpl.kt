package ru.practicum.android.diploma.domain.interactors.impl

import ru.practicum.android.diploma.data.dto.filterdto.FilterAreaDto
import ru.practicum.android.diploma.data.repositories.AreasRepository
import ru.practicum.android.diploma.domain.models.filtermodels.Region
import ru.practicum.android.diploma.domain.models.vacancy.Country

class RegionsRepositoryImpl(
    private val areasRepository: AreasRepository
) : RegionsRepository {

    override suspend fun getRegionsByCountry(countryId: Int): List<Region> {
        val allAreas = areasRepository.getAllAreas() ?: return emptyList()
        val countryDto = allAreas.firstOrNull { it.id == countryId } ?: return emptyList()
        val country = Country(id = countryDto.id, name = countryDto.name)
        return collectRegions(countryDto, country)
    }

    override suspend fun getAllRegions(): List<Region> {
        return areasRepository.getAllAreas()?.flatMap { area ->
            collectAreaWithCountry(area)
        } ?: emptyList()
    }

    private fun collectAreaWithCountry(area: FilterAreaDto): List<Region> {
        val country = if (area.parentId == null || area.parentId == 0) {
            Country(area.id, area.name)
        } else null
        return collectRegions(area, country)
    }

    private fun collectRegions(area: FilterAreaDto, country: Country?): List<Region> {
        if (area.areas.isEmpty()) return listOf(Region(area.id, area.name, country))
        val currentCountry = if (area.parentId == null || area.parentId == 0) Country(area.id, area.name) else country
        return area.areas.flatMap { collectRegions(it, currentCountry) }
    }
}
