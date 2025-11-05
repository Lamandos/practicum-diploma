package ru.practicum.android.diploma.domain.interactors.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.api.repositories.FavoritesRepository
import ru.practicum.android.diploma.domain.interactors.FavoritesInteractor
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import java.sql.SQLException

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
        } catch (e: SQLException) {
            // Ошибки базы данных
            false
        } catch (e: IllegalStateException) {
            // Ошибки состояния приложения
            false
        }
    }
}
