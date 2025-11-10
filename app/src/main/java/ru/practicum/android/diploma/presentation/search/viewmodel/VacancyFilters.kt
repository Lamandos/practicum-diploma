package ru.practicum.android.diploma.presentation.search.viewmodel

data class VacancyFilters(
    val region: String? = null,
    val industry: String? = null,
    val salary: Int? = null,
    val hideWithoutSalary: Boolean = false
)

