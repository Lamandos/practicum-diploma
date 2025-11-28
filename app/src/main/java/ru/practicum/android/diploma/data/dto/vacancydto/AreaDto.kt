package ru.practicum.android.diploma.data.dto.vacancydto

data class AreaDto(
    val id: Int,
    val countryId: Int? = null,

    val name: String,
    val country: String? = null
)
