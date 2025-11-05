package ru.practicum.android.diploma.domain.api.repositories

import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

interface VacancyRepository {
    suspend fun getVacancyDetails(vacancyId: String): VacancyDetails?
}
