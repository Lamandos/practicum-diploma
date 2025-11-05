package ru.practicum.android.diploma.data.mappers

import ru.practicum.android.diploma.data.dto.vacancydetailsdto.ContactsDto
import ru.practicum.android.diploma.domain.models.vacancydetails.Contacts

fun ContactsDto.toDomain(id: String): Contacts? {
    return if (name != null || email != null || !phones.isNullOrEmpty()) {
        Contacts(
            id = id,
            name = name,
            email = email,
            phones = phones?.mapNotNull { phoneDto ->
                phoneDto.number?.takeIf { it.isNotBlank() }?.let { number ->
                    Contacts.Phone(
                        number = number,
                        comment = phoneDto.comment
                    )
                }
            }?.takeIf { it.isNotEmpty() }
        )
    } else {
        null
    }
}

fun ContactsDto.PhoneDto.toDomain(): Contacts.Phone = Contacts.Phone(
    number = number ?: "",
    comment = comment
)
