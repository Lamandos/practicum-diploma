package ru.practicum.android.diploma.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import ru.practicum.android.diploma.domain.models.vacancydetails.Experience

class ExperienceConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): Experience? {
        if (value == null || value.isEmpty()) return null
        return try {
            gson.fromJson(value, Experience::class.java)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun fromExperience(experience: Experience?): String {
        return gson.toJson(experience ?: return "")
    }
}
