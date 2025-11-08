package ru.practicum.android.diploma.data.dto.vacancydetailsdto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ContactsDto(
    val name: String?,
    val email: String?,
    val phones: List<PhoneDto>?
) {
    @kotlinx.serialization.Serializable
    data class PhoneDto(
        @SerialName("formatted") val formatted: String,
        val comment: String? = null
    )
}
