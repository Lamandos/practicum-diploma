package ru.practicum.android.diploma.data.mappers

import ru.practicum.android.diploma.data.network.VacancyItem
import ru.practicum.android.diploma.domain.models.vacancydetails.Salary
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

object VacancyMapper {

    fun mapToVacancyDetails(items: List<VacancyItem>): List<VacancyDetails> {
        return items.map { item ->
            VacancyDetails(
                id = item.id,
                name = item.name,
                description = "",
                salary = item.salary?.let { s ->
                    Salary(from = s.from, to = s.to, currency = s.currency)
                },
                address = null,
                experience = "",
                schedule = "",
                employment = "",
                employer = item.employer.name,
                contacts = null,
                area = item.area.name,
                skills = emptyList(),
                url = "",
                industry = "",
                publishedAt = null
            )
        }
    }
}
