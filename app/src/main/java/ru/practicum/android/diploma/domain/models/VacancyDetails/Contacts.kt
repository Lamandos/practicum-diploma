package ru.practicum.android.diploma.domain.models.VacancyDetails

import android.provider.ContactsContract

data class Contacts(
    val name: String?,
    val email: String?,
    val phones: List<ContactsContract.CommonDataKinds.Phone>?,
)
