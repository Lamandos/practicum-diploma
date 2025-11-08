package ru.practicum.android.diploma.data.db

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ru.practicum.android.diploma.domain.models.filtermodels.FilterArea
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.domain.models.vacancydetails.Address
import ru.practicum.android.diploma.domain.models.vacancydetails.Contacts
import ru.practicum.android.diploma.domain.models.vacancydetails.Employer
import ru.practicum.android.diploma.domain.models.vacancydetails.Employment
import ru.practicum.android.diploma.domain.models.vacancydetails.Experience
import ru.practicum.android.diploma.domain.models.vacancydetails.Salary
import ru.practicum.android.diploma.domain.models.vacancydetails.Schedule
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

class Mappers(
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Contacts::class.java, ContactsTypeAdapter())
        .create()
) {

    companion object {
        private const val TAG = "Mappers"
        private const val JSON_SYNTAX_ERROR = "JSON syntax error deserializing contacts: "
        private const val JSON_PARSE_ERROR = "JSON parse error deserializing contacts: "
        private const val ILLEGAL_STATE_ERROR = "Illegal state deserializing contacts: "
    }

    fun toVacancyDetails(entity: FavoritesEntity): VacancyDetails {
        val contacts = parseContacts(entity.contacts)

        return VacancyDetails(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            salary = parseField<Salary>(entity.salary),
            address = parseField<Address>(entity.address),
            experience = parseField<Experience>(entity.experience),
            schedule = parseField<Schedule>(entity.schedule),
            employment = parseField<Employment>(entity.employment),
            employer = parseField<Employer>(entity.employer),
            contacts = contacts,
            area = parseField<FilterArea>(entity.area),
            skills = entity.skills,
            url = entity.url,
            industry = parseField<FilterIndustry>(entity.industry),
            publishedAt = entity.published,
        )
    }

    fun toFavoritesEntity(vacancy: VacancyDetails): FavoritesEntity {
        return FavoritesEntity(
            id = vacancy.id,
            name = vacancy.name,
            description = vacancy.description,
            salary = serializeField(vacancy.salary),
            address = serializeField(vacancy.address),
            experience = serializeField(vacancy.experience),
            schedule = serializeField(vacancy.schedule),
            employment = serializeField(vacancy.employment),
            employer = serializeField(vacancy.employer),
            area = serializeField(vacancy.area),
            skills = vacancy.skills,
            url = vacancy.url,
            industry = serializeField(vacancy.industry),
            published = vacancy.publishedAt,
            contacts = gson.toJson(vacancy.contacts)
        )
    }

    private inline fun <reified T> parseField(json: String?): T? {
        return json?.let { gson.fromJson(it, T::class.java) }
    }

    private fun serializeField(obj: Any?): String? {
        return obj?.let { gson.toJson(it) }
    }

    private fun parseContacts(contactsJson: String?): Contacts? {
        return contactsJson?.let { json ->
            try {
                gson.fromJson(json, Contacts::class.java)
            } catch (e: Exception) {
                handleContactsDeserializationError(e)
                null
            }
        }
    }

    private fun handleContactsDeserializationError(e: Exception) {
        val errorMessage = when (e) {
            is com.google.gson.JsonSyntaxException -> JSON_SYNTAX_ERROR + e.message
            is com.google.gson.JsonParseException -> JSON_PARSE_ERROR + e.message
            is IllegalStateException -> ILLEGAL_STATE_ERROR + e.message
            else -> "Unknown error deserializing contacts: ${e.message}"
        }
        Log.e(TAG, errorMessage)
    }
}
