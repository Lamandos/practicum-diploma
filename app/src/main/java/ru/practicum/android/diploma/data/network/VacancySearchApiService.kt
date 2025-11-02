package ru.practicum.android.diploma.data.network

import retrofit2.http.GET
import retrofit2.http.QueryMap

interface VacancySearchApiService {
    @GET("/vacancies")
    suspend fun searchVacancies(@QueryMap options: Map<String, String>): VacancySearchResponse
}
