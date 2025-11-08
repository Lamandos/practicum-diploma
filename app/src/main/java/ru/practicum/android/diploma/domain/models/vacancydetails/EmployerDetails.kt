package ru.practicum.android.diploma.domain.models.vacancydetails

data class EmployerDetails(
    val id: String,
    val name: String,
    val logo: String,
    val description: String?,
    val siteUrl: String?,
)
