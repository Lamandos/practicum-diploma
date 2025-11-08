package ru.practicum.android.diploma.domain.interactors.impl

import ru.practicum.android.diploma.domain.api.repositories.VacancyRepository
import ru.practicum.android.diploma.domain.interactors.VacancyInteractor
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

class VacancyInteractorImpl(
    private val vacancyRepository: VacancyRepository
) : VacancyInteractor {

    override suspend fun getVacancyDetails(vacancyId: String): VacancyDetails? {
        return vacancyRepository.getVacancyDetails(vacancyId)
    }
}
