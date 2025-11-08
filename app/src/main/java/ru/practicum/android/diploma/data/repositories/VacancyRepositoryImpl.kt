package ru.practicum.android.diploma.data.repositories

import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.domain.api.repositories.VacancyRepository
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

class VacancyRepositoryImpl constructor(
    private val networkClient: NetworkClient
) : VacancyRepository {

    override suspend fun getVacancyDetails(vacancyId: String): VacancyDetails? {
        return networkClient.getVacancyDetails(vacancyId)
    }
}
