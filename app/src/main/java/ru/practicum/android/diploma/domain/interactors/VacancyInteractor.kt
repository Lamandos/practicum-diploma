package ru.practicum.android.diploma.domain.interactors

import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

interface VacancyInteractor {
    suspend fun getVacancyDetails(vacancyId: String): VacancyDetails?
}
