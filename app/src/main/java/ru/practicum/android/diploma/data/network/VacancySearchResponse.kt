package ru.practicum.android.diploma.data.network

import ru.practicum.android.diploma.domain.models.vacancy.Area
import ru.practicum.android.diploma.domain.models.vacancy.Salary
import ru.practicum.android.diploma.domain.models.vacancy.Vacancy
import ru.practicum.android.diploma.domain.models.vacancydetails.EmployerDetails

data class VacancySearchResponse(
    val items: List<VacancyItem>,
    val page: Int,
    val pages: Int,
    val perPage: Int,
    val found: Int
)

data class VacancyItem(
    val id: String,
    val name: String,
    val employer: EmployerDetails,
    val area: Area,
    val salary: Salary?
)

fun VacancyItem.toDomain(): Vacancy {
    return Vacancy(
        id = id,
        name = name,
        salary = salary,
        employer = employer,
        area = area,
        publishedAt = "",
        snippet = null
    )
}
