package ru.practicum.android.diploma.data.dto.vacancydetailsdto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactsDto(
    @SerialName("id") val id: String?,
    @SerialName("name") val name: String?,
    @SerialName("email") val email: String?,
    @SerialName("phones") val phones: List<PhoneDto>?
) {
    @Serializable
    data class PhoneDto(
        @SerialName("formatted") val formatted: String?,
        @SerialName("comment") val comment: String? = null
    )
}
