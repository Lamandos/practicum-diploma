package ru.practicum.android.diploma.data.db

import com.google.gson.Gson
import ru.practicum.android.diploma.domain.models.filtermodels.FilterArea
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.domain.models.vacancydetails.Address
import ru.practicum.android.diploma.domain.models.vacancydetails.Employer
import ru.practicum.android.diploma.domain.models.vacancydetails.Employment
import ru.practicum.android.diploma.domain.models.vacancydetails.Experience
import ru.practicum.android.diploma.domain.models.vacancydetails.Salary
import ru.practicum.android.diploma.domain.models.vacancydetails.Schedule
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

class Mappers(private val gson: Gson = Gson()) {

    fun toVacancyDetails(entity: FavoritesEntity): VacancyDetails = VacancyDetails(
        id = entity.id,
        name = entity.name,
        description = entity.description,
        salary = entity.salary?.let { gson.fromJson(it, Salary::class.java) },
        address = entity.address?.let { gson.fromJson(it, Address::class.java) },
        experience = entity.experience?.let { gson.fromJson(it, Experience::class.java) },
        schedule = entity.schedule?.let { gson.fromJson(it, Schedule::class.java) },
        employment = entity.employment?.let { gson.fromJson(it, Employment::class.java) },
        employer = entity.employer?.let { gson.fromJson(it, Employer::class.java) },
        contacts = null,
        area = entity.area?.let { gson.fromJson(it, FilterArea::class.java) },
        skills = entity.skills,
        url = entity.url,
        industry = entity.industry?.let { gson.fromJson(it, FilterIndustry::class.java) },
        publishedAt = entity.published
    )

    fun toFavoritesEntity(vacancy: VacancyDetails): FavoritesEntity = FavoritesEntity(
        id = vacancy.id,
        name = vacancy.name,
        description = vacancy.description,
        salary = gson.toJson(vacancy.salary),
        address = gson.toJson(vacancy.address),
        experience = gson.toJson(vacancy.experience),
        schedule = gson.toJson(vacancy.schedule),
        employment = gson.toJson(vacancy.employment),
        employer = gson.toJson(vacancy.employer),
        area = gson.toJson(vacancy.area),
        skills = vacancy.skills,
        url = vacancy.url,
        industry = gson.toJson(vacancy.industry),
        published = vacancy.publishedAt
    )
}
