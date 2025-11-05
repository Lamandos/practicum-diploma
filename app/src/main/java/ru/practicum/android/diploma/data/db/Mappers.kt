package ru.practicum.android.diploma.data.db

import ru.practicum.android.diploma.domain.models.vacancydetails.Address
import ru.practicum.android.diploma.domain.models.vacancydetails.Salary
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import ru.practicum.android.diploma.domain.models.filtermodels.FilterArea
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.domain.models.vacancydetails.Employer
import ru.practicum.android.diploma.domain.models.vacancydetails.Employment
import ru.practicum.android.diploma.domain.models.vacancydetails.Experience
import ru.practicum.android.diploma.domain.models.vacancydetails.Schedule
import com.google.gson.Gson

class Mappers(private val gson: Gson = Gson()) {

    fun FavoritesEntity.toModel(): VacancyDetails = VacancyDetails(
        id = id,
        name = name,
        description = description,
        salary = salary?.let { gson.fromJson(it, Salary::class.java) },
        address = address?.let { gson.fromJson(it, Address::class.java) },
        experience = experience?.let { gson.fromJson(it, Experience::class.java) },
        schedule = schedule?.let { gson.fromJson(it, Schedule::class.java) },
        employment = employment?.let { gson.fromJson(it, Employment::class.java) },
        employer = employer?.let { gson.fromJson(it, Employer::class.java) },
        contacts = null,
        area = area?.let { gson.fromJson(it, FilterArea::class.java) },
        skills = skills,
        url = url,
        industry = industry?.let { gson.fromJson(it, FilterIndustry::class.java) },
        publishedAt = published
    )

    fun VacancyDetails.toEntity(): FavoritesEntity = FavoritesEntity(
        id = id,
        name = name,
        description = description,
        salary = gson.toJson(salary),
        address = gson.toJson(address),
        experience = gson.toJson(experience),
        schedule = gson.toJson(schedule),
        employment = gson.toJson(employment),
        employer = gson.toJson(employer),
        area = gson.toJson(area),
        skills = skills,
        url = url,
        industry = gson.toJson(industry),
        published = publishedAt
    )
}
