package ru.practicum.android.diploma.domain.api.repositories

import ru.practicum.android.diploma.domain.models.FilterModels.Industry
import ru.practicum.android.diploma.domain.models.FilterModels.Region
import ru.practicum.android.diploma.domain.models.FilterModels.VacancyFilters
import ru.practicum.android.diploma.domain.models.Vacancy.Vacancy
import ru.practicum.android.diploma.domain.models.VacancyDetails.VacancyDetails

interface VacanciesRepository {
    // поиск вакансий

    suspend fun searchVacancies(
        query: String, // поисковый запрос
        page: Int = 1, // номер страницы для постраничной загрузки
        pageSize: Int,
        filters: VacancyFilters?,
    ): Result<List<Vacancy>>

    // получение деталей вакансии
    suspend fun getVacancyDetails(vacancyId: String): Result<VacancyDetails>

    // получение списка отраслей
    suspend fun getIndustries(): Result<List<Industry>>

    // получение списка регионов
    suspend fun getRegions(countryCode: String? = null): Result<List<Region>>

    // проверка доступности сети
    suspend fun isNetworkAvailable(): Boolean
}
