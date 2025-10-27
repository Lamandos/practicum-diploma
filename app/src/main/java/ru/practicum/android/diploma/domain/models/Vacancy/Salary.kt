package ru.practicum.android.diploma.domain.models.Vacancy

data class Salary(
    val from: Int?,
    val to: Int?,
    val currency: String?,
    val gross: Boolean?
)
