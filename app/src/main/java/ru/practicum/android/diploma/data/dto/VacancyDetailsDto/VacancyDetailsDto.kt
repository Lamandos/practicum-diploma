package ru.practicum.android.diploma.data.dto.VacancyDetailsDto

import ru.practicum.android.diploma.data.dto.VacancyDto.AreaDto
import ru.practicum.android.diploma.data.dto.VacancyDto.SalaryDto

data class VacancyDetailsDto(
    val id: String,
    val name: String,
    val salary: SalaryDto?,
    val employer: EmployerDetailsDto,
    val area: AreaDto,
    val description: String,
    val key_skills: List<KeySkillDto>,
    val experience: ExperienceDto?,
    val employment: EmploymentDto?,
)
