package ru.practicum.android.diploma.data.mappers

import ru.practicum.android.diploma.data.dto.filterdto.FilterAreaDto
import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto
import ru.practicum.android.diploma.data.dto.vacancydetailsdto.EmployerDto
import ru.practicum.android.diploma.data.dto.vacancydetailsdto.EmploymentDto
import ru.practicum.android.diploma.data.dto.vacancydetailsdto.ExperienceDto
import ru.practicum.android.diploma.data.dto.vacancydetailsdto.SalaryDto
import ru.practicum.android.diploma.data.dto.vacancydetailsdto.ScheduleDto
import ru.practicum.android.diploma.data.dto.vacancydetailsdto.SkillDto
import ru.practicum.android.diploma.data.dto.vacancydetailsdto.VacancyDetailsDto
import ru.practicum.android.diploma.data.network.VacancyItem
import ru.practicum.android.diploma.domain.models.filtermodels.FilterArea
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.domain.models.vacancydetails.Employer
import ru.practicum.android.diploma.domain.models.vacancydetails.Employment
import ru.practicum.android.diploma.domain.models.vacancydetails.Experience
import ru.practicum.android.diploma.domain.models.vacancydetails.Salary
import ru.practicum.android.diploma.domain.models.vacancydetails.Schedule
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

fun FilterAreaDto.toDomain(): FilterArea = FilterArea(
    id = this.id,
    name = this.name,
    parentId = 0,
    areas = emptyArray()
)

fun EmployerDto.toDomain(): Employer = Employer(
    id = this.id,
    name = this.name,
    logo = this.logo
)

// ИСПРАВИТЬ - убрать .name
fun List<SkillDto>.toDomainList(): List<String> = this.map { it.name }

fun ExperienceDto.toDomain(): Experience = Experience(
    id = this.id,
    name = this.name
)

fun ScheduleDto.toDomain(): Schedule = Schedule(
    id = this.id,
    name = this.name
)

fun FilterIndustryDto.toDomain(): FilterIndustry = FilterIndustry(
    id = this.id,
    name = this.name
)

fun EmploymentDto.toDomain(): Employment = Employment(
    id = this.id,
    name = this.name
)

fun SalaryDto.toDomain(): Salary = Salary(
    from = this.from,
    to = this.to,
    currency = this.currency,
)

object VacancyMapper {

    fun mapToVacancyDetails(items: List<VacancyItem>): List<VacancyDetails> {
        return items.map { item ->
            VacancyDetails(
                id = item.id,
                name = item.name,
                description = "",
                salary = item.salary?.let { s ->
                    Salary(from = s.from, to = s.to, currency = s.currency)
                },
                address = null,
                experience = null,
                schedule = null,
                employment = null,
                employer = Employer(
                    id = item.employer.id,
                    name = item.employer.name,
                    logo = item.employer.logo
                ),
                contacts = null,
                area = FilterArea(
                    id = item.area.id,
                    name = item.area.name,
                    parentId = 0,
                    areas = emptyArray()
                ),
                skills = emptyList(),
                url = "",
                industry = null,
                publishedAt = null
            )
        }
    }

    fun mapToDomain(dto: VacancyDetailsDto): VacancyDetails {
        return VacancyDetails(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            salary = dto.salary?.toDomain(),
            address = dto.address?.toDomain(),
            experience = dto.experience.toDomain(),
            schedule = dto.schedule.toDomain(),
            employment = dto.employment.toDomain(),
            employer = dto.employer.toDomain(),
            contacts = dto.contacts?.toDomain(dto.id),
            area = dto.area.toDomain(),
            publishedAt = dto.publishedAt,
            skills = dto.skills ?: emptyList(),
            url = dto.url,
            industry = dto.industry?.toDomain()
        )
    }
}
