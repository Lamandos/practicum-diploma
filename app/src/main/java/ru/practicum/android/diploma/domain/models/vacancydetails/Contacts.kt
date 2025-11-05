package ru.practicum.android.diploma.domain.models.vacancydetails

data class Contacts(
    val id: String,
    val name: String?,
    val email: String?,
    val phones: List<Phone>?
) {
    data class Phone(
        val number: String,
        val comment: String? = null
    )
}
