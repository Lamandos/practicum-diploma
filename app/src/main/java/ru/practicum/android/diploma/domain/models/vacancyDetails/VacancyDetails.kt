package ru.practicum.android.diploma.domain.models.vacancyDetails

import android.provider.ContactsContract
import ru.practicum.android.diploma.domain.models.vacancy.Area
import ru.practicum.android.diploma.domain.models.vacancy.Salary

data class VacancyDetails(
    val id: String,
    val name: String,
    val salary: Salary?,
    val employer: EmployerDetails,
    val area: Area,
    val description: String, // HTML content
    val keySkills: List<KeySkill>,
    val experience: Expirience?,
    val employment: Employment?,
    val contacts: ContactsContract.Contacts?,
)
