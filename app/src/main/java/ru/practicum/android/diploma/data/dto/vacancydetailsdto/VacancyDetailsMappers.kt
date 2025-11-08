package ru.practicum.android.diploma.data.dto.vacancydetailsdto

import android.util.Log
import ru.practicum.android.diploma.domain.models.vacancydetails.Contacts
import ru.practicum.android.diploma.domain.models.vacancydetails.Experience
import ru.practicum.android.diploma.domain.models.vacancydetails.Salary

private const val TAG = "VacancyDetailsMappers"

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
    Log.d(TAG, "ContactsDto: id='${this.id}', name='${this.name}', email='${this.email}'")

    logPhonesDetails()

    val domainPhones = mapPhonesToDomain()
    val shouldCreateContacts = shouldCreateContacts(domainPhones)

    return if (shouldCreateContacts) {
        Contacts(
            id = this.id ?: vacancyId,
            name = name,
            email = email,
            phones = domainPhones
        )
    } else {
        null
    }
}

private fun ContactsDto.logPhonesDetails() {
    this.phones?.forEachIndexed { index, phoneDto ->
        Log.d(TAG, "PhoneDto $index: formatted='${phoneDto.formatted}', comment='${phoneDto.comment}'")
    }
}

private fun ContactsDto.mapPhonesToDomain(): List<Contacts.Phone> {
    return phones?.mapNotNull { phoneDto ->
        if (!phoneDto.formatted.isNullOrBlank()) {
            Contacts.Phone(
                number = phoneDto.formatted,
                comment = phoneDto.comment
            )
        } else {
            null
        }
    } ?: emptyList()
}

private fun ContactsDto.shouldCreateContacts(domainPhones: List<Contacts.Phone>): Boolean {
    return domainPhones.isNotEmpty() || name != null || email != null
}
