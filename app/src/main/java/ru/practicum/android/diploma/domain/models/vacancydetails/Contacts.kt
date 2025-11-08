package ru.practicum.android.diploma.domain.models.vacancydetails

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Contacts(
    val id: String,
    val name: String? = null,
    val email: String? = null,
    val phones: List<Phone>? = null
) {
    // Вычисляемое свойство для безопасного доступа к телефонам
    val safePhones: List<Phone>
        get() = phones ?: emptyList()

    @kotlinx.serialization.Serializable
    data class Phone(
        @SerialName("formatted") val number: String,
        val comment: String? = null
    )
}
