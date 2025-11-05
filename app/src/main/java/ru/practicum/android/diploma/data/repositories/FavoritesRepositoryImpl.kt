package ru.practicum.android.diploma.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.data.db.AppDataBase
import ru.practicum.android.diploma.data.db.Mappers
import ru.practicum.android.diploma.domain.api.repositories.FavoritesRepository
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

class FavoritesRepositoryImpl(
    private val database: AppDataBase,
    private val mappers: Mappers
) : FavoritesRepository {

    override fun getAllFavorites(): Flow<List<VacancyDetails>> {
        return database.favoritesDao().getAllVacancies().map { entities ->
            entities.map { entity ->
                mappers.toVacancyDetails(entity)
            }
        }
    }

    override suspend fun addToFavorites(vacancy: VacancyDetails) {
        try {
            val entity = mappers.toFavoritesEntity(vacancy)
            database.favoritesDao().insertVacancy(entity)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun getVacancyById(vacancyId: String): VacancyDetails? {
        return try {
            val entity = database.favoritesDao().getVacancyById(vacancyId)
            entity?.let { mappers.toVacancyDetails(it) }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun isFavorite(vacancyId: String): Boolean {
        return try {
            val result = database.favoritesDao().isFavorite(vacancyId)
            result
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun removeFromFavorites(vacancyId: String) {
        try {
            database.favoritesDao().deleteVacancyById(vacancyId)
        } catch (e: Exception) {
            throw e
        }
    }
}
