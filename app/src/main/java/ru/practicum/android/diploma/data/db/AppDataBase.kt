package ru.practicum.android.diploma.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [FavoritesEntity::class], version = 3)
@TypeConverters(ListConverter::class, ExperienceConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao

    companion object {
        fun getDb(context: Context): AppDataBase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "favorites.db"
            ).fallbackToDestructiveMigration()
                .build()
        }
    }
}
