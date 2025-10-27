package ru.practicum.android.diploma.data.dto.vacancyDto

data class VacancyDto(
    val id: String,
    val name: String,
    val salary: SalaryDto?,
    val employer: EmployerDto,
    val area: AreaDto,
    val publishedAt: String,
    val snippet: SnippetDto?,
)
