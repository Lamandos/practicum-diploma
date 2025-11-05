package ru.practicum.android.diploma.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import ru.practicum.android.diploma.domain.models.filtermodels.FilterArea
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.domain.models.vacancydetails.Employer
import ru.practicum.android.diploma.domain.models.vacancydetails.Experience
import ru.practicum.android.diploma.domain.models.vacancydetails.Salary

class ObjectConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromSalary(salary: Salary?): String? = gson.toJson(salary)

    @TypeConverter
    fun toSalary(json: String?): Salary? = json?.let { gson.fromJson(it, Salary::class.java) }

    @TypeConverter
    fun fromExperience(experience: Experience?): String? = gson.toJson(experience)

    @TypeConverter
    fun toExperience(json: String?): Experience? = json?.let { gson.fromJson(it, Experience::class.java) }

    @TypeConverter
    fun fromEmployer(employer: Employer?): String? = gson.toJson(employer)

    @TypeConverter
    fun toEmployer(json: String?): Employer? = json?.let { gson.fromJson(it, Employer::class.java) }

    @TypeConverter
    fun fromArea(area: FilterArea?): String? = gson.toJson(area)

    @TypeConverter
    fun toArea(json: String?): FilterArea? = json?.let { gson.fromJson(it, FilterArea::class.java) }

    @TypeConverter
    fun fromIndustry(industry: FilterIndustry?): String? = gson.toJson(industry)

    @TypeConverter
    fun toIndustry(json: String?): FilterIndustry? = json?.let { gson.fromJson(it, FilterIndustry::class.java) }
}
