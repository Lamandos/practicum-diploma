package ru.practicum.android.diploma.data.dto.vacancydetailsdto

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


