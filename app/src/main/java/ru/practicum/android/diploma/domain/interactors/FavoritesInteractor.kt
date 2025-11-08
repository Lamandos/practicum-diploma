package ru.practicum.android.diploma.domain.interactors

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

interface FavoritesInteractor {
    suspend fun toggleFavorite(vacancy: VacancyDetails)
    fun getAllFavorites(): Flow<List<VacancyDetails>>
    suspend fun getVacancyById(id: String): VacancyDetails?
    suspend fun addToFavorites(vacancy: VacancyDetails)
    suspend fun removeFromFavorites(id: String)
    suspend fun isFavorite(id: String): Boolean
    suspend fun updateFavorite(vacancy: VacancyDetails)
}
