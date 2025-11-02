package ru.practicum.android.diploma.data.dto.vacancydto

data class AreaDto(
    val id: String,
    val countryId: String? = null,

    val name: String,
    val country: String? = null
)

