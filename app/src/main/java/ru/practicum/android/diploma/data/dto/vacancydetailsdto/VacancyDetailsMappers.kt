package ru.practicum.android.diploma.data.dto.vacancydetailsdto

import ru.practicum.android.diploma.domain.models.vacancydetails.Contacts
import ru.practicum.android.diploma.domain.models.vacancydetails.Experience
import ru.practicum.android.diploma.domain.models.vacancydetails.Phone
import ru.practicum.android.diploma.domain.models.vacancydetails.Salary

fun ExperienceDto.toDomain(): Experience {
    return Experience(
        id = id,
        name = name
    )
}

fun SalaryDto.toDomain(): Salary? {
    return if (from != null || to != null) {
        Salary(
            from = from,
            to = to,
            currency = currency
        )
    } else {
        null
    }
}

fun ContactsDto.toDomain(id: String): Contacts? {
    return if (name != null || email != null || !phones.isNullOrEmpty()) {
        Contacts(
            id = id,
            name = name,
            email = email,
            phones = phones?.mapNotNull { phoneDto ->
                phoneDto.number?.let { number ->
                    Phone(
                        number = number,
                        comment = phoneDto.comment
                    )
                }
            }?.takeIf { it.isNotEmpty() } as List<Contacts.Phone>?
        )
    } else {
        null
    }
}
