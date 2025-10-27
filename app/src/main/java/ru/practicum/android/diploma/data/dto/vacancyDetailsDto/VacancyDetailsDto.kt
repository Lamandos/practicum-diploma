package ru.practicum.android.diploma.data.dto.vacancyDetailsDto

import ru.practicum.android.diploma.data.dto.vacancyDto.AreaDto
import ru.practicum.android.diploma.data.dto.vacancyDto.SalaryDto

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
