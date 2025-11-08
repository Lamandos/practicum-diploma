package ru.practicum.android.diploma.data.db

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import ru.practicum.android.diploma.domain.models.vacancydetails.Contacts

class ContactsTypeAdapter : TypeAdapter<Contacts>() {

    override fun write(out: JsonWriter, value: Contacts) {
        out.beginObject()
        out.name("id").value(value.id)
        value.name?.let { out.name("name").value(it) }
        value.email?.let { out.name("email").value(it) }

        out.name("phones").beginArray()
        value.safePhones.forEach { phone ->
            out.beginObject()
            out.name("formatted").value(phone.number)
            phone.comment?.let { out.name("comment").value(it) }
            out.endObject()
        }
        out.endArray()
        out.endObject()
    }

    override fun read(reader: JsonReader): Contacts {
        var id = ""
        var name: String? = null
        var email: String? = null
        var phones: List<Contacts.Phone>? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "id" -> id = reader.nextString()
                "name" -> name = reader.nextString()
                "email" -> email = reader.nextString()
                "phones" -> phones = readPhones(reader)
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return Contacts(id = id, name = name, email = email, phones = phones)
    }

    private fun readPhones(reader: JsonReader): List<Contacts.Phone> {
        val phones = mutableListOf<Contacts.Phone>()
        reader.beginArray()
        while (reader.hasNext()) {
            var number = ""
            var comment: String? = null
            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    "formatted" -> number = reader.nextString()
                    "comment" -> comment = reader.nextString()
                    else -> reader.skipValue()
                }
            }
            reader.endObject()
            if (number.isNotBlank()) {
                phones.add(Contacts.Phone(number = number, comment = comment))
            }
        }
        reader.endArray()
        return phones
    }
}
