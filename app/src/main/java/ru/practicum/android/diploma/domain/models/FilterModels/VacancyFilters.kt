package ru.practicum.android.diploma.domain.models.FilterModels

data class VacancyFilters(
    val region: Region? = null,
    val industry: Industry? = null,
    val salary: Int? = null,
    val hideWithoutSalary: Boolean = false,
    val currency: String = "RUR",
)
