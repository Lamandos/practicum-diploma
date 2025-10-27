package ru.practicum.android.diploma.domain.api.repositories

import ru.practicum.android.diploma.domain.models.Vacancy.Vacancy
import ru.practicum.android.diploma.domain.models.VacancyDetails.VacancyDetails

interface FavoritesRepository {

    // добавление вакансии в избранное
    suspend fun addToFavorites(vacancy: Vacancy)

    // удаление вакансии из избранного
    suspend fun removeFromFavorites(vacancyId: String)

    // получение списка избранных вакансий с пагинацией
    suspend fun getFavorites(
        page: Int,
        pageSize: Int,
    ): Result<List<Vacancy>>

    // получение всех избранных вакансий для оффлайн режима
    suspend fun getAllFavorites(): List<Vacancy>

    // получение деталей избранной вакансии для оффлайн режима
    suspend fun getFavoriteVacancyDetails(vacancyId: String): Result<VacancyDetails>

    // проверка, добавлена ли вакансия в избранное
    suspend fun isFavorite(vacancyId: String): Boolean
}
