package ru.practicum.android.diploma.data.repositories

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.data.db.AppDataBase
import ru.practicum.android.diploma.data.db.Mappers
import ru.practicum.android.diploma.domain.api.repositories.FavoritesRepository
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import java.sql.SQLException

private const val TAG = "FavoritesRepository"

class FavoritesRepositoryImpl(
    private val database: AppDataBase,
    private val mappers: Mappers
) : FavoritesRepository {

    override fun getAllFavorites(): Flow<List<VacancyDetails>> {
        return database.favoritesDao().getAllVacancies().map { entities ->
            entities.map { mappers.toVacancyDetails(it) }
        }
    }

    override suspend fun addToFavorites(vacancy: VacancyDetails) {
        try {
            val entity = mappers.toFavoritesEntity(vacancy)
            database.favoritesDao().insertVacancy(entity)
        } catch (e: SQLException) {
            Log.e(TAG, "SQL error adding vacancy to favorites: ${vacancy.id}", e)
            throw e
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Illegal state adding vacancy to favorites: ${vacancy.id}", e)
            throw e
        }
    }

    override suspend fun getVacancyById(vacancyId: String): VacancyDetails? {
        return try {
            val entity = database.favoritesDao().getVacancyById(vacancyId)
            entity?.let { mappers.toVacancyDetails(it) }
        } catch (e: SQLException) {
            Log.e(TAG, "SQL error getting vacancy by id: $vacancyId", e)
            null
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Illegal state getting vacancy by id: $vacancyId", e)
            null
        }
    }

    override suspend fun isFavorite(vacancyId: String): Boolean {
        return try {
            database.favoritesDao().isFavorite(vacancyId)
        } catch (e: SQLException) {
            Log.e(TAG, "SQL error checking favorite status: $vacancyId", e)
            false
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Illegal state checking favorite status: $vacancyId", e)
            false
        }
    }

    override suspend fun removeFromFavorites(vacancyId: String) {
        try {
            database.favoritesDao().deleteVacancyById(vacancyId)
        } catch (e: SQLException) {
            Log.e(TAG, "SQL error removing vacancy from favorites: $vacancyId", e)
            throw e
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Illegal state removing vacancy from favorites: $vacancyId", e)
            throw e
        }
    }
}
