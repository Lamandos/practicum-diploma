package ru.practicum.android.diploma.data.network

import retrofit2.http.GET
import ru.practicum.android.diploma.data.dto.filterdto.FilterAreaDto

interface AreasApiService {
    @GET("areas")
    suspend fun getAreas(): List<FilterAreaDto>
}
