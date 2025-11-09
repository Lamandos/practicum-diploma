package ru.practicum.android.diploma.data.repositories

import android.graphics.Region
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.mappers.VacancyMapper
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.VacancySearchRequest
import ru.practicum.android.diploma.data.network.VacancySearchResponse
import ru.practicum.android.diploma.domain.api.repositories.VacanciesRepository
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import java.io.IOException

class SearchVacanciesRepositoryImpl(
    private val networkClient: NetworkClient,
) : VacanciesRepository {

    private var totalFound: Int = 0

    override val totalFoundCount: Int
        get() = totalFound

    override suspend fun searchVacancies(
        query: String,
        page: Int,
        pageSize: Int,
        filters: VacancyFilters? // Меняем на VacancyFilters?
    ): Result<List<VacancyDetails>> {
        return try {
            // Пока не используем фильтры в запросе, но сохраняем для будущего использования
            val searchQuery = buildSearchQuery(query, filters)

            val response = networkClient.doRequest(
                VacancySearchRequest(
                    text = searchQuery,
                    page = page,
                    perPage = pageSize
                )
            )

            if (response is ResponseSuccess<*>) {
                val data = response.data as VacancySearchResponse
                totalFound = data.found
                val vacancies = VacancyMapper.mapToVacancyDetails(data.items)
                Result.success(vacancies)
            } else {
                Result.failure(Exception("Network error"))
            }
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }

    // Вспомогательный метод для построения поискового запроса с фильтрами
    private fun buildSearchQuery(baseQuery: String, filters: VacancyFilters?): String {
        var query = baseQuery

        filters?.let {
            // Добавляем регион/страну
            if (it.region != null) {
                query += " ${it.region}"
            }

            // Добавляем отрасль
            if (it.industry != null) {
                query += " ${it.industry.name}"
            }

            // Добавляем зарплату (если нужно)
            if (it.salary != null) {
                query += " зарплата ${it.salary} ${it.currency}"
            }

            // hideWithoutSalary пока не используем, так как это требует поддержки на сервере
        }

        return query
    }

    override suspend fun getVacancyDetails(vacancyId: String): Result<VacancyDetails> =
        networkClient.getVacancyDetails(vacancyId)?.let { Result.success(it) }
            ?: Result.failure(Exception("Не удалось получить данные вакансии"))

    override suspend fun getIndustries(): Result<List<VacancyDetails>> =
        Result.failure(Exception("Not implemented"))

    override suspend fun getRegions(countryCode: String?): Result<List<Region>> =
        Result.failure(Exception("Not implemented"))

    override suspend fun isNetworkAvailable(): Boolean = true
}
