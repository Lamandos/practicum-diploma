package ru.practicum.android.diploma.domain.models.filtermodels

data class FilterArea(
    val id: Int,
    val name: String,
    val parentId: Int?,
    val areas: Array<FilterArea>
)
