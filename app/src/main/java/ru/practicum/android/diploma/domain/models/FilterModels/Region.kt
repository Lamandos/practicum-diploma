package ru.practicum.android.diploma.domain.models.FilterModels

import ru.practicum.android.diploma.domain.models.Vacancy.Country

data class Region(
    val id: String,
    val name: String,
    val country: Country?,
)
