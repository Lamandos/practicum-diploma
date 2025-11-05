package ru.practicum.android.diploma.data.network

import ru.practicum.android.diploma.data.dto.Response
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

interface NetworkClient {

    suspend fun doRequest(dto: Any): Response
    suspend fun getVacancyDetails(vacancyId: String): VacancyDetails?
}
