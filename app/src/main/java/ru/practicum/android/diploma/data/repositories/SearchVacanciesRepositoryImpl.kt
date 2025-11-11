package ru.practicum.android.diploma.data.repositories

import android.graphics.Region
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.mappers.VacancyMapper
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.VacancySearchRequest
import ru.practicum.android.diploma.data.network.VacancySearchResponse
import ru.practicum.android.diploma.domain.api.repositories.VacanciesRepository
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import java.io.IOException
import java.net.UnknownHostException

class SearchVacanciesRepositoryImpl(
    private val networkClient: NetworkClient,
) : VacanciesRepository {

    private var totalFound: Int = 0

    override val totalFoundCount: Int
        get() = totalFound

    companion object {
        private const val TAG = "SearchRepository"
        private const val ERROR_INVALID_RESPONSE = "Invalid response data type"
        private const val ERROR_NETWORK = "Network error"
        private const val MAX_LOG_VACANCIES = 5
    }

    override suspend fun searchVacancies(
        query: String,
        page: Int,
        pageSize: Int,
        filters: VacancyFilters?,
    ): Result<List<VacancyDetails>> {
        return try {
            val searchRequest = createSearchRequestWithSalaryFilter(query, page, pageSize, filters)

            println("DEBUG: Repository - sending request without salary to API")

            val response = networkClient.doRequest(searchRequest)
            when (response) {
                is ResponseSuccess<*> -> {
                    val data = response.data as? VacancySearchResponse
                    if (data != null) {
                        totalFound = data.found
                        var vacancies = VacancyMapper.mapToVacancyDetails(data.items)
                        println("DEBUG: Repository - received ${vacancies.size} vacancies from API")

                        // ДОБАВЛЯЕМ ФИЛЬТРАЦИЮ ПО ЗАРПЛАТЕ ЗДЕСЬ
                        if (filters != null && filters.salary != null) {
                            val originalCount = vacancies.size
                            vacancies = filterVacanciesBySalary(vacancies, filters)
                            println("DEBUG: Salary filtering - from $originalCount to ${vacancies.size} vacancies")

                            // Логируем информацию о фильтрации
                            logSalaryFilteringInfo(vacancies, filters.salary)
                        }

                        Result.success(vacancies)
                    } else {
                        Result.failure(Exception(ERROR_INVALID_RESPONSE))
                    }
                }
                else -> {
                    Result.failure(Exception("$ERROR_NETWORK: $response"))
                }
            }
        } catch (e: UnknownHostException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: IllegalStateException) {
            Result.failure(e)
        }
    }

    private fun filterVacanciesBySalary(
        vacancies: List<VacancyDetails>,
        filters: VacancyFilters
    ): List<VacancyDetails> {
        return vacancies.filter { vacancy ->
            matchesSalaryFilter(vacancy.salary, filters.salary, filters.hideWithoutSalary == true)
        }
    }

    private fun matchesSalaryFilter(
        vacancySalary: ru.practicum.android.diploma.domain.models.vacancydetails.Salary?,
        filterSalary: Int?,
        hideWithoutSalary: Boolean
    ): Boolean {
        if (filterSalary == null) return true
        if (vacancySalary == null) return !hideWithoutSalary

        val from = vacancySalary.from
        val to = vacancySalary.to

        // Упрощенная логика для уменьшения сложности
        return when {
            from != null && to != null -> filterSalary in from..to
            from != null -> from <= filterSalary
            to != null -> filterSalary <= to
            else -> !hideWithoutSalary
        }
    }

    private fun logSalaryFilteringInfo(vacancies: List<VacancyDetails>, targetSalary: Int) {
        println("=== SALARY FILTERING INFO ===")
        println("Target salary: $targetSalary")
        println("Found ${vacancies.size} vacancies after filtering")

        vacancies.take(MAX_LOG_VACANCIES).forEachIndexed { index, vacancy ->
            val salary = vacancy.salary
            val salaryText = when {
                salary?.from != null && salary.to != null -> "${salary.from}-${salary.to}"
                salary?.from != null -> "от ${salary.from}"
                salary?.to != null -> "до ${salary.to}"
                else -> "не указана"
            }
            println("$index: $salaryText - MATCH")
        }
        println("=============================")
    }

    private fun createSearchRequestWithSalaryFilter(
        query: String,
        page: Int,
        pageSize: Int,
        filters: VacancyFilters?
    ): VacancySearchRequest {
        return VacancySearchRequest(
            text = query,
            page = page,
            perPage = pageSize,
            area = filters?.region?.id?.toInt(),
            industry = filters?.industry?.id,
            salaryfrom = null, // Исправлено: salary вместо salaryfrom
            onlyWithSalary = filters?.hideWithoutSalary
        )
    }

    override suspend fun getVacancyDetails(vacancyId: String): Result<VacancyDetails> {
        return networkClient.getVacancyDetails(vacancyId)?.let {
            Result.success(it)
        } ?: Result.failure(Exception("Vacancy details not found"))
    }

    override suspend fun getIndustries(): Result<List<VacancyDetails>> =
        Result.failure(Exception("Not implemented"))

    override suspend fun getRegions(countryCode: String?): Result<List<Region>> =
        Result.failure(Exception("Not implemented"))

    override suspend fun isNetworkAvailable(): Boolean = true
}
