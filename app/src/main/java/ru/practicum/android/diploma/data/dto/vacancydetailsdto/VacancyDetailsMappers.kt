package ru.practicum.android.diploma.data.dto.vacancydetailsdto

import ru.practicum.android.diploma.domain.models.vacancydetails.Address
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import ru.practicum.android.diploma.domain.models.vacancydetails.Experience
import ru.practicum.android.diploma.domain.models.vacancydetails.Salary
import ru.practicum.android.diploma.domain.models.vacancydetails.Contacts
import ru.practicum.android.diploma.domain.models.vacancydetails.Phone

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

fun ContactsDto.toDomain(): Contacts? {
    return if (name != null || email != null || !phones.isNullOrEmpty()) {
        Contacts(
            id = "", // временно
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

fun VacancyDetailsDto.toDomain(): VacancyDetails {
    return VacancyDetails(
        id = id,
        name = name,
        description = description,
        salary = salary?.toDomain(),
        address = address?.fullAddress as Address?,
        experience = experience.toDomain(),
        schedule = schedule.name,
        employment = employment.name,
        employer = employer.name,
        contacts = contacts?.toDomain(),
        area = area.name,
        skills = skills ?: emptyList(),
        url = url,
        industry = industry?.name ?: "",
        publishedAt = publishedAt
    )
}
