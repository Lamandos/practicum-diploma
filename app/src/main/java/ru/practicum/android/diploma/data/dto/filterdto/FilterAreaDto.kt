package ru.practicum.android.diploma.data.dto.filterdto

data class FilterAreaDto(
    val id: Int,
    val name: String,
    val parentId: Int?,
    val areas: Array<FilterAreaDto>
)
