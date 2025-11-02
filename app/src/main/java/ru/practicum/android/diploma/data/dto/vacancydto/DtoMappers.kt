package ru.practicum.android.diploma.data.dto.vacancydto

import ru.practicum.android.diploma.domain.models.vacancy.Area
import ru.practicum.android.diploma.domain.models.vacancy.Country
import ru.practicum.android.diploma.domain.models.vacancy.Employer
import ru.practicum.android.diploma.domain.models.vacancy.Salary
import ru.practicum.android.diploma.domain.models.vacancy.Snippet

fun EmployerDto.toDomain(): Employer {
    return Employer(
        id = id,
        name = name,
        logoUrls = logoUrls
    )
}

fun SalaryDto.toDomain(): Salary {
    return Salary(
        from = from,
        to = to,
        currency = currency,
        gross = gross
    )
}

fun SnippetDto.toDomain(): Snippet {
    return Snippet(
        requirement = requirement,
        responsibility = responsibility
    )
}

fun AreaDto.toDomain(): Area {
    return Area(
        id = id,
        name = name,
        country = if (country != null && countryId != null) Country(id = countryId, name = country) else null
    )
}
