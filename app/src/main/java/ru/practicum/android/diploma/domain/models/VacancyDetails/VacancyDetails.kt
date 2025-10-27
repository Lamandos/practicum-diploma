package ru.practicum.android.diploma.domain.models.VacancyDetails

import android.provider.ContactsContract
import ru.practicum.android.diploma.domain.models.Vacancy.Area
import ru.practicum.android.diploma.domain.models.Vacancy.Salary

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
