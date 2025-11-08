package ru.practicum.android.diploma.data.repositories

import ru.practicum.android.diploma.data.dto.ResponseError
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto
import ru.practicum.android.diploma.data.network.NetworkClient

class IndustriesRepository(private val networkClient: NetworkClient) {

    suspend fun getAllIndustries(): List<FilterIndustryDto>? {
        return when (val response = networkClient.getIndustries()) {
            is ResponseSuccess<*> -> response.data as? List<FilterIndustryDto>
            is ResponseError -> null
            else -> null
        }
    }
}
