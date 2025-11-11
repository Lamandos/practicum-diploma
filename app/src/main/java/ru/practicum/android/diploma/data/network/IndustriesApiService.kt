package ru.practicum.android.diploma.data.network

import retrofit2.http.GET
import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto

interface IndustriesApiService {
    @GET("industries")
    suspend fun getIndustries(): List<FilterIndustryDto>
}
