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

        fun collectRegions(area: FilterAreaDto): List<Region> {
            val regions = mutableListOf<Region>()
            for (child in area.areas) {
                if (child.areas.isEmpty()) {
                    regions.add(Region(id = child.id, name = child.name, country = country))
                } else {
                    regions.addAll(collectRegions(child))
                }
            }
            return regions
        }

        return collectRegions(countryDto)
    }

    override suspend fun getAllRegions(): List<Region> {
        val allAreas = areasRepository.getAllAreas() ?: return emptyList()
        val regions = mutableListOf<Region>()

        fun collectRegions(area: FilterAreaDto, country: Country?) {
            for (child in area.areas) {
                if (child.areas.isEmpty()) {
                    regions.add(Region(id = child.id, name = child.name, country = country))
                } else {
                    val childCountry = if (area.parentId == null || area.parentId == 0) {
                        Country(id = area.id, name = area.name)
                    } else {
                        country
                    }
                    collectRegions(child, childCountry)
                }
            }
        }

        allAreas.forEach { area ->
            if (area.parentId == null || area.parentId == 0) {
                val country = Country(id = area.id, name = area.name)
                collectRegions(area, country)
            } else {
                collectRegions(area, null)
            }
        }

        return regions
    }
}
