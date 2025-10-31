package ru.practicum.android.diploma.domain.api.repositories


import android.graphics.Region
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

interface VacanciesRepository {
    // поиск вакансий

    suspend fun searchVacancies(
        query: String, // поисковый запрос
        page: Int = 1, // номер страницы для постраничной загрузки
        pageSize: Int,
        filters: FilterIndustry,
    ): Result<List<VacancyDetails>>

    // получение деталей вакансии
    suspend fun getVacancyDetails(vacancyId: String): Result<VacancyDetails>

    // получение списка отраслей
    suspend fun getIndustries(): Result<List<VacancyDetails>>

    // получение списка регионов
    suspend fun getRegions(countryCode: String? = null): Result<List<Region>>

    // проверка доступности сети
    suspend fun isNetworkAvailable(): Boolean
}
