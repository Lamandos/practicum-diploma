package ru.practicum.android.diploma.data.dto.vacancydetailsdto

data class ContactsDto(
    val name: String?,
    val email: String?,
    val phones: List<PhoneDto>?
) {
    data class PhoneDto(
        val number: String,
        val comment: String? = null
    )
}
