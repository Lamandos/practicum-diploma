package ru.practicum.android.diploma.data.repositories

import ru.practicum.android.diploma.data.dto.ResponseError
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto
import ru.practicum.android.diploma.data.network.FilterIndustryRequest
import ru.practicum.android.diploma.data.network.FilterIndustryResponse
import ru.practicum.android.diploma.data.network.NetworkClient

class IndustriesRepository(private val networkClient: NetworkClient) {

    suspend fun getAllIndustries(): List<FilterIndustryDto>? {
        return when (val response = networkClient.getIndustries(FilterIndustryRequest())) {
            is ResponseSuccess<*> -> {
                (response.data as? FilterIndustryResponse)?.industries
            }
            is ResponseError -> null
            else -> null
        }
    }
}
