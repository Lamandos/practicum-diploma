package ru.practicum.android.diploma.data.repositories

import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.domain.api.repositories.IndustryRepository

class IndustryRepositoryImpl(
    private val networkClient: NetworkClient
) : IndustryRepository {

    override suspend fun getAllIndustries(): List<FilterIndustryDto>? {
        val response = networkClient.getIndustries(Unit)
        return if (response is ru.practicum.android.diploma.data.dto.ResponseSuccess<*>) {
            response.data as? List<FilterIndustryDto>
        } else {
            null
        }
    }
}
