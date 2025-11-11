package ru.practicum.android.diploma.data.mappers

import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry

object IndustryMapper {

    fun mapDtoToDomain(dto: FilterIndustryDto): FilterIndustry {
        return FilterIndustry(
            id = dto.id,
            name = dto.name
        )
    }

    fun mapDtoListToDomain(dtoList: List<FilterIndustryDto>): List<FilterIndustry> {
        return dtoList.map { mapDtoToDomain(it) }
    }
}
