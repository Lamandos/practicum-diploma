package ru.practicum.android.diploma.domain.models.filtermodels

import ru.practicum.android.diploma.domain.models.vacancy.Country

data class Region(
    val id: Int,
    val name: String,
    val country: Country?,
)
