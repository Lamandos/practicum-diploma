package ru.practicum.android.diploma.domain.models.mappers

import ru.practicum.android.diploma.domain.models.vacancy.Area
import ru.practicum.android.diploma.domain.models.vacancy.Salary
import ru.practicum.android.diploma.domain.models.vacancy.Vacancy
import ru.practicum.android.diploma.domain.models.vacancydetails.EmployerDetails
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

object DomainMappers {
    fun VacancyDetails.toVacancy(): Vacancy {
        return Vacancy(
            id = this.id,
            name = this.name,
            salary = this.salary?.let {
                Salary(
                    from = it.from,
                    to = it.to,
                    currency = it.currency,
                    gross = null
                )
            },
            employer = EmployerDetails(
                id = this.employer?.id ?: "",
                name = this.employer?.name ?: "",
                logo = this.employer?.logo ?: "",
                description = null,
                siteUrl = null
            ),
            area = Area(
                id = this.area?.id ?: 0,
                name = this.area?.name ?: "",
                country = null
            ),
            publishedAt = this.publishedAt ?: "",
            snippet = null, // не пытаемся парсить description
            address = this.address
        )
    }
}
