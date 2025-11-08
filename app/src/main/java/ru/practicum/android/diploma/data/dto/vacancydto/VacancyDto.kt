package ru.practicum.android.diploma.data.dto.vacancydto

import ru.practicum.android.diploma.domain.models.vacancy.Snippet

data class VacancyDto(
    val id: String,
    val name: String,
    val salary: SalaryDto?,
    val employer: EmployerDto,
    val area: AreaDto,
    val publishedAt: String = "",
    val snippet: Snippet? = null
)
