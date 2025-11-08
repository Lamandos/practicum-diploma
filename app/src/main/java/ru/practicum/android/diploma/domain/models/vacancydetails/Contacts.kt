package ru.practicum.android.diploma.domain.models.vacancydetails

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Contacts(
    val id: String,
    val name: String?,
    val email: String?,
    val phones: List<Phone>? = null
) {
    @kotlinx.serialization.Serializable
    data class Phone(
        @SerialName("formatted") val number: String,
        val comment: String? = null
    )
}
