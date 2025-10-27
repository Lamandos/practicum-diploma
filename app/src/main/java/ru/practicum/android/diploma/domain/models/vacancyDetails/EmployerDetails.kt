package ru.practicum.android.diploma.domain.models.vacancyDetails

data class EmployerDetails(
    val id: String,
    val name: String,
    val logoUrls: String,
    val description: String?,
    val siteUrl: String?,
)
