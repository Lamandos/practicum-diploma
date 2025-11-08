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
            } catch (e: com.google.gson.JsonSyntaxException) {
                Log.e("Mappers", "JSON syntax error deserializing contacts: ${e.message}")
                null
            } catch (e: com.google.gson.JsonParseException) {
                Log.e("Mappers", "JSON parse error deserializing contacts: ${e.message}")
                null
            } catch (e: IllegalStateException) {
                Log.e("Mappers", "Illegal state deserializing contacts: ${e.message}")
                null
            }
        }
    }
}
