package ru.practicum.android.diploma.domain.models.vacancy

data class Vacancy(
    val id: String,
    val name: String,
    val salary: Salary?,
    val employer: Employer,
    val area: Area,
    val publishedAt: String,
    val snippet: Snippet?,
)
