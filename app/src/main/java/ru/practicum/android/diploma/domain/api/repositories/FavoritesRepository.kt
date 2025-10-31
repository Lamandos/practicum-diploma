package ru.practicum.android.diploma.domain.api.repositories

import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

interface FavoritesRepository {

    // добавление вакансии в избранное
    suspend fun addToFavorites(vacancy: VacancyDetails)

    // удаление вакансии из избранного
    suspend fun removeFromFavorites(vacancy: VacancyDetails)

    // получение списка избранных вакансий с пагинацией
    suspend fun getFavorites(
        page: Int,
        pageSize: Int,
    ): Result<List<VacancyDetails>>

    // получение всех избранных вакансий для оффлайн режима
    suspend fun getAllFavorites(): List<VacancyDetails>

    // получение деталей избранной вакансии для оффлайн режима
    suspend fun getFavoriteVacancyDetails(vacancyId: String): Result<VacancyDetails>

    // проверка, добавлена ли вакансия в избранное
    suspend fun isFavorite(vacancyId: String): Boolean
}
