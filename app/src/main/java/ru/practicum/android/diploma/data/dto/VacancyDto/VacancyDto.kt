package ru.practicum.android.diploma.data.dto.VacancyDto

data class VacancyDto(
    val id: String,
    val name: String,
    val salary: SalaryDto?,
    val employer: EmployerDto,
    val area: AreaDto,
    val published_at: String,
    val snippet: SnippetDto?
)
