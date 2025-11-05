// FavoritesInteractorImpl.kt
package ru.practicum.android.diploma.domain.interactors.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.api.repositories.FavoritesRepository
import ru.practicum.android.diploma.domain.interactors.FavoritesInteractor
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
) : FavoritesInteractor {

    override fun getAllFavorites(): Flow<List<VacancyDetails>> {
        return favoritesRepository.getAllFavorites()
    }

    override suspend fun getVacancyById(vacancyId: String): VacancyDetails? {
        return favoritesRepository.getVacancyById(vacancyId)
    }

    override suspend fun addToFavorites(vacancy: VacancyDetails) {
        try {
            favoritesRepository.addToFavorites(vacancy)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun removeFromFavorites(vacancyId: String) {
        try {
            favoritesRepository.removeFromFavorites(vacancyId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun isFavorite(vacancyId: String): Boolean {
        return try {
            val result = favoritesRepository.isFavorite(vacancyId)
            result
        } catch (e: Exception) {
            false
        }
    }
}
