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
        println("DEBUG: FavoritesRepository - getAllFavorites called")
        return database.favoritesDao().getAllVacancies().map { entities ->
            println("DEBUG: FavoritesRepository - got ${entities.size} entities from database")
            entities.map { entity ->
                mappers.toVacancyDetails(entity)
            }
        }
    }

    override suspend fun addToFavorites(vacancy: VacancyDetails) {
        println("DEBUG: FavoritesRepository - addToFavorites called for vacancy: ${vacancy.id}")
        try {
            val entity = mappers.toFavoritesEntity(vacancy)
            println("DEBUG: FavoritesRepository - entity created: $entity")
            database.favoritesDao().insertVacancy(entity)
            println("DEBUG: FavoritesRepository - vacancy added successfully")
        } catch (e: Exception) {
            println("DEBUG: FavoritesRepository - ERROR adding to favorites: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun getVacancyById(vacancyId: String): VacancyDetails? {
        println("DEBUG: FavoritesRepository - getVacancyById called: $vacancyId")
        return try {
            val entity = database.favoritesDao().getVacancyById(vacancyId)
            println("DEBUG: FavoritesRepository - entity found: ${entity != null}")
            entity?.let { mappers.toVacancyDetails(it) }
        } catch (e: Exception) {
            println("DEBUG: FavoritesRepository - ERROR getting vacancy: ${e.message}")
            null
        }
    }

    override suspend fun isFavorite(vacancyId: String): Boolean {
        println("DEBUG: FavoritesRepository - isFavorite called: $vacancyId")
        return try {
            val result = database.favoritesDao().isFavorite(vacancyId)
            println("DEBUG: FavoritesRepository - isFavorite result: $result")
            result
        } catch (e: Exception) {
            println("DEBUG: FavoritesRepository - ERROR checking favorite: ${e.message}")
            false
        }
    }

    override suspend fun removeFromFavorites(vacancyId: String) {
        println("DEBUG: FavoritesRepository - removeFromFavorites called: $vacancyId")
        try {
            database.favoritesDao().deleteVacancyById(vacancyId)
            println("DEBUG: FavoritesRepository - vacancy removed successfully")
        } catch (e: Exception) {
            println("DEBUG: FavoritesRepository - ERROR removing from favorites: ${e.message}")
            throw e
        }
    }
}
