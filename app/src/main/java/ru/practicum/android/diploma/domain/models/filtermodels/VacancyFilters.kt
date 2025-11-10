package ru.practicum.android.diploma.domain.models.filtermodels

data class VacancyFilters(
    val region: Region? = null,
    val industry: Industry? = null,
    val salary: Int? = null,
    val hideWithoutSalary: Boolean = false,
    val currency: String = "RUB"
)

fun VacancyFilters.isAnyFilterApplied(): Boolean {
    return region != null || industry != null || salary != null || hideWithoutSalary
}
