package ru.practicum.android.diploma.data.mappers

import ru.practicum.android.diploma.data.dto.vacancydetailsdto.ContactsDto
import ru.practicum.android.diploma.domain.models.vacancydetails.Contacts

fun ContactsDto.toDomain(id: String): Contacts = Contacts(
    id = id,
    name = name,
    email = email,
    phones = phones?.map { it.toDomain() }
)

fun ContactsDto.PhoneDto.toDomain(): Contacts.Phone = Contacts.Phone(
    number = number,
    comment = comment
)
