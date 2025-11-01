package ru.practicum.android.diploma.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Insert
    suspend fun insertVacancy(item: FavoritesEntity)

    @Query("SELECT * FROM favorites")
    fun getAllVacancies(): Flow<List<FavoritesEntity>>

    @Delete
    suspend fun deleteVacancy(item: FavoritesEntity)

    @Query("SELECT * FROM favorites WHERE id = :id")
    suspend fun getVacancyById(id: String): FavoritesEntity?

    @Query("SELECT EXISTS(SELECT * FROM favorites WHERE id = :id)")
    suspend fun isFavorite(id: String): Boolean

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteVacancyById(id: String)
}
