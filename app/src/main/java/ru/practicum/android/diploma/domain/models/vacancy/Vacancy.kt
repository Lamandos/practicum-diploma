package ru.practicum.android.diploma.domain.models.vacancy

import ru.practicum.android.diploma.domain.models.vacancydetails.Address
import ru.practicum.android.diploma.domain.models.vacancydetails.EmployerDetails

data class Vacancy(
    val id: String,
    val name: String,
    val salary: Salary?,
    val employer: EmployerDetails,
    val area: Area,
    val publishedAt: String = "",
    val snippet: Snippet? = null,
    val address: Address?
)
