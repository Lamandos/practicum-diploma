package ru.practicum.android.diploma.data.mappers

import ru.practicum.android.diploma.data.dto.filterdto.FilterAreaDto
import ru.practicum.android.diploma.domain.models.filtermodels.Region
import ru.practicum.android.diploma.domain.models.vacancy.Country

fun FilterAreaDto.toRegion(country: Country? = null): Region =
    Region(
        id = this.id,
        name = this.name,
        country = country
    )

fun List<FilterAreaDto>.toRegions(country: Country? = null): List<Region> {
    val result = mutableListOf<Region>()
    this.forEach { dto ->
        result.add(dto.toRegion(country))
        if (dto.areas.isNotEmpty()) {
            result.addAll(dto.areas.toList().toRegions(country))
        }
    }
    return result
}
