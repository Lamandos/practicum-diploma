package ru.practicum.android.diploma.data.db

import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

class Mappers {

    fun toVacancyDetails(entity: FavoritesEntity): VacancyDetails = VacancyDetails(
        id = entity.id,
        name = entity.name,
        description = entity.description,
        salary = null,
        address = null,
        experience = entity.experience,
        schedule = entity.schedule,
        employer = entity.employer,
        contacts = null,
        area = entity.area,
        skills = entity.skills,
        url = entity.url,
        industry = entity.industry,
        publishedAt = entity.published,
        employment = entity.employment
    )

    fun toFavoritesEntity(vacancy: VacancyDetails): FavoritesEntity = FavoritesEntity(
        id = vacancy.id,
        name = vacancy.name,
        description = vacancy.description,
        salary = null,
        address = null,
        experience = vacancy.experience,
        schedule = vacancy.schedule,
        employment = vacancy.employment,
        employer = vacancy.employer,
        area = vacancy.area,
        skills = vacancy.skills,
        url = vacancy.url,
        industry = vacancy.industry,
        published = vacancy.publishedAt
    )
}
