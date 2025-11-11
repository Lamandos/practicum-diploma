package ru.practicum.android.diploma.data.repositories

import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto
import ru.practicum.android.diploma.data.mappers.IndustryMapper
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.domain.api.repositories.IndustryRepository
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry

class IndustryRepositoryImpl(
    private val networkClient: NetworkClient
) : IndustryRepository {

    override suspend fun getAllIndustries(): List<FilterIndustry>? {
        val response = networkClient.getIndustries(Unit)
        return if (response is ru.practicum.android.diploma.data.dto.ResponseSuccess<*>) {
            val dtoList = response.data as? List<FilterIndustryDto>
            dtoList?.let { IndustryMapper.mapDtoListToDomain(it) }
        } else {
            null
        }
    }
}
