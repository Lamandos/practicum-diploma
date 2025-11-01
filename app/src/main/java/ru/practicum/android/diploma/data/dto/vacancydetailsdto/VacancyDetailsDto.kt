package ru.practicum.android.diploma.data.dto.vacancydetailsdto

import ru.practicum.android.diploma.data.dto.filterdto.FilterAreaDto
import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto

data class VacancyDetailsDto(
    val id: String,
    val name: String,
    val description: String,
    val salary: SalaryDto?,
    val address: AddressDto?,
    val experience: ExperienceDto,
    val schedule: ScheduleDto,
    val employment: EmploymentDto,
    val contacts: ContactsDto?,
    val employer: EmployerDto,
    val area: FilterAreaDto,
    val publishedAt: String?,
    val skills: List<SkillDto>?,
    val url: String,
    val industry: FilterIndustryDto?
)
