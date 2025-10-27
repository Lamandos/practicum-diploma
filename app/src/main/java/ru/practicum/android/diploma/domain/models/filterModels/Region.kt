package ru.practicum.android.diploma.domain.models.filterModels

import ru.practicum.android.diploma.domain.models.vacancy.Country

data class Region(
    val id: String,
    val name: String,
    val country: Country?,
)
