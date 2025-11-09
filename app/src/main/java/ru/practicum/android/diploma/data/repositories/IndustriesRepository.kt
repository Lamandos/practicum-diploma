package ru.practicum.android.diploma.data.repositories

import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto
import ru.practicum.android.diploma.data.network.NetworkClient

class IndustriesRepository(
    private val networkClient: NetworkClient
) {

    suspend fun getAllIndustries(): List<FilterIndustryDto>? {
        val response = networkClient.getIndustries(Unit)
        return if (response is ru.practicum.android.diploma.data.dto.ResponseSuccess<*>) {
            response.data as? List<FilterIndustryDto>
        } else {
            null
        }
    }
}
