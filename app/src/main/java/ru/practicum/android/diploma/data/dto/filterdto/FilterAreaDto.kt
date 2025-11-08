package ru.practicum.android.diploma.data.dto.filterdto

data class FilterAreaDto(
    val id: String,
    val name: String,
    val parentId: Int?,
    val areas: Array<FilterAreaDto>
)
