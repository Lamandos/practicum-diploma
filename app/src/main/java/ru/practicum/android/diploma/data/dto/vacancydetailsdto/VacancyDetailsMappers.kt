package ru.practicum.android.diploma.data.dto.vacancydetailsdto

import android.util.Log
import ru.practicum.android.diploma.domain.models.vacancydetails.Contacts
import ru.practicum.android.diploma.domain.models.vacancydetails.Experience
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

fun ContactsDto.toDomain(vacancyId: String): Contacts? {
    Log.d(
        "VacancyDetailsMappers",
        "ContactsDto: id='${this.id}', name='${this.name}', email='${this.email}', phones=${this.phones}"
    )

    this.phones?.forEachIndexed { index, phoneDto ->
        Log.d(
            "VacancyDetailsMappers",
            "PhoneDto $index: formatted='${phoneDto.formatted}', comment='${phoneDto.comment}'"
        )
    }

    return if (name != null || email != null || !phones.isNullOrEmpty()) {
        val domainPhones = phones?.mapNotNull { phoneDto ->
            if (!phoneDto.formatted.isNullOrBlank()) {
                Contacts.Phone(
                    number = phoneDto.formatted,
                    comment = phoneDto.comment
                )
            } else {
                null
            }
        } ?: emptyList()

        // Показываем контакты только если есть реальные данные
        if (domainPhones.isNotEmpty() || name != null || email != null) {
            Contacts(
                id = this.id ?: vacancyId,
                name = name,
                email = email,
                phones = domainPhones
            )
        } else {
            null
        }
    } else {
        null
    }
}
