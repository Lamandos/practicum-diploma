package ru.practicum.android.diploma.domain.models.vacancydetails

import ru.practicum.android.diploma.domain.models.filtermodels.FilterArea
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry

data class VacancyDetails(
    val id: String,
    val name: String,
    val description: String,
    val salary: Salary?,
    val address: Address?,
    val experience: Experience,
    val schedule: Schedule,
    val employment: Employment,
    val employer: Employer,
    val contacts: Contacts?,
    val area: FilterArea,
    val skills: List<String>,
    val url: String,
    val industry: FilterIndustry?,
    val publishedAt: String? = null
)
