package ru.practicum.android.diploma.domain.interactors.impl

import android.util.Log
import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.api.repositories.FavoritesRepository
import ru.practicum.android.diploma.domain.interactors.FavoritesInteractor
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

private const val TAG = "FavoritesInteractor"

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
        favoritesRepository.addToFavorites(vacancy)
    }

    override suspend fun removeFromFavorites(vacancyId: String) {
        favoritesRepository.removeFromFavorites(vacancyId)
    }

    override suspend fun isFavorite(vacancyId: String): Boolean {
        return try {
            favoritesRepository.isFavorite(vacancyId)
        } catch (e: RuntimeException) {
            Log.e(TAG, "Error checking favorite status for vacancy: $vacancyId", e)
            false
        }
    }
}
