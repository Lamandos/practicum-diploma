package ru.practicum.android.diploma.domain.interactors.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.data.db.Mappers
import ru.practicum.android.diploma.domain.api.repositories.FavoritesRepository
import ru.practicum.android.diploma.domain.interactors.FavoritesInteractor
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository,
    private val mappers: Mappers
) : FavoritesInteractor {

    override fun getAllFavorites(): Flow<List<VacancyDetails>> {
        return favoritesRepository.getAllFavorites()
    }

    override suspend fun getVacancyById(vacancyId: String): VacancyDetails? {
        val entity = favoritesRepository.getById(vacancyId)
        return entity?.let { mappers.toVacancyDetails(it) }
    }

    override suspend fun addToFavorites(vacancy: VacancyDetails) {
        favoritesRepository.addToFavorites(vacancy)
    }

    override suspend fun removeFromFavorites(vacancyId: String) {
        favoritesRepository.removeFromFavorites(vacancyId)
    }

    override suspend fun isFavorite(vacancyId: String): Boolean {
        return favoritesRepository.isFavorite(vacancyId)
    }

    override suspend fun updateFavorite(vacancy: VacancyDetails) {
        favoritesRepository.addToFavorites(vacancy)
    }

    override suspend fun toggleFavorite(vacancy: VacancyDetails) {
        val existing = favoritesRepository.getById(vacancy.id)
        if (existing == null) {
            favoritesRepository.addToFavorites(vacancy)
        } else {
            favoritesRepository.removeFromFavorites(vacancy.id)
        }
    }
}

