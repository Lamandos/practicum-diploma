package ru.practicum.android.diploma.domain.models.vacancydetails

data class VacancyDetails(
    val id: String,
    val name: String,
    val description: String,
    val salary: Salary?,
    val address: Address?,
    val experience: Experience,
    val schedule: String,
    val employment: String,
    val employer: String,
    val contacts: Contacts?,
    val area: String,
    val skills: List<String>,
    val url: String,
    val industry: String,
    val publishedAt: String? = null
)
