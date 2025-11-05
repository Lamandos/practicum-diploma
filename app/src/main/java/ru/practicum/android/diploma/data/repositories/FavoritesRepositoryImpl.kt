package ru.practicum.android.diploma.data.repositories

import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.data.db.AppDataBase
import ru.practicum.android.diploma.data.db.Mappers
import ru.practicum.android.diploma.domain.api.repositories.FavoritesRepository
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import java.sql.SQLException

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
            // Ошибки базы данных
            throw e
        } catch (e: IllegalStateException) {
            // Ошибки состояния (например, попытка вставить дубликат)
            throw e
        }
        // УДАЛИТЬ блок catch (e: Exception) и e.printStackTrace()
    }

    override suspend fun getVacancyById(vacancyId: String): VacancyDetails? {
        return try {
            val entity = database.favoritesDao().getVacancyById(vacancyId)
            entity?.let { mappers.toVacancyDetails(it) }
        } catch (e: SQLException) {
            // Ошибки базы данных
            null
        } catch (e: IllegalStateException) {
            // Ошибки состояния
            null
        }
    }

    override suspend fun isFavorite(vacancyId: String): Boolean {
        return try {
            database.favoritesDao().isFavorite(vacancyId)
        } catch (e: SQLException) {
            // Ошибки базы данных
            false
        } catch (e: IllegalStateException) {
            // Ошибки состояния
            false
        }
    }

    override suspend fun removeFromFavorites(vacancyId: String) {
        try {
            database.favoritesDao().deleteVacancyById(vacancyId)
        } catch (e: SQLException) {
            // Ошибки базы данных
            throw e
        } catch (e: IllegalStateException) {
            // Ошибки состояния
            throw e
        }
    }
}
