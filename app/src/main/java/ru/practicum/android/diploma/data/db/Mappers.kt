package ru.practicum.android.diploma.data.db

import ru.practicum.android.diploma.domain.models.vacancydetails.Address
import ru.practicum.android.diploma.domain.models.vacancydetails.Salary
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

class Mappers {
    fun FavoritesEntity.toModel(): VacancyDetails = VacancyDetails(
        id = id,
        name = name,
        description = description,
        salary = salary as? Salary,
        address = address as? Address,
        experience = experience,
        schedule = schedule,
        employer = employer,
        contacts = null,
        area = area,
        skills = skills,
        url = url,
        industry = industry,
        publishedAt = published,
        employment = employment
    )

    fun VacancyDetails.toEntity(): FavoritesEntity = FavoritesEntity(
        id = id,
        name = name,
        description = description,
        salary = salary as? String,
        address = address as? String,
        experience = experience,
        schedule = schedule,
        employment = employment,
        employer = employer,
        area = area,
        skills = skills,
        url = url,
        industry = industry,
        published = publishedAt
    )
}
