// FavoritesRepository.kt
package ru.practicum.android.diploma.domain.api.repositories

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

interface FavoritesRepository {
    suspend fun addToFavorites(vacancy: VacancyDetails)
    suspend fun removeFromFavorites(vacancyId: String)
    suspend fun getVacancyById(vacancyId: String): VacancyDetails?
    fun getAllFavorites(): Flow<List<VacancyDetails>>
    suspend fun isFavorite(vacancyId: String): Boolean
}
