package ru.practicum.android.diploma.data.dto.vacancydetailsdto

import ru.practicum.android.diploma.data.dto.vacancydto.AreaDto
import ru.practicum.android.diploma.data.dto.vacancydto.SalaryDto

data class VacancyDetailsDto(
    val id: String,
    val name: String,
    val salary: SalaryDto?,
    val employer: EmployerDetailsDto,
    val area: AreaDto,
    val description: String,
    val keySkills: List<KeySkillDto>,
    val experience: ExperienceDto?,
    val employment: EmploymentDto?,
)
