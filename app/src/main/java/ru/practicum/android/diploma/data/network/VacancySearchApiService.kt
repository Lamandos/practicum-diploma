package ru.practicum.android.diploma.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

interface VacancySearchApiService {
    @GET("/vacancies")
    suspend fun searchVacancies(@QueryMap options: Map<String, String>): VacancySearchResponse

    @GET("/vacancies/{id}")
    suspend fun getVacancyDetails(
        @Path("id") id: String
    ): VacancyDetails
}
