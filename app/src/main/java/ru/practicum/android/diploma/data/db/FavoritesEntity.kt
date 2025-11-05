package ru.practicum.android.diploma.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.practicum.android.diploma.domain.models.vacancydetails.Experience

@Entity(tableName = "favorites")
data class FavoritesEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "salary")
    val salary: String?, // JSON строка
    @ColumnInfo(name = "address")
    val address: String?, // JSON строка
    @ColumnInfo(name = "experience")
    val experience: String?, // JSON строка
    @ColumnInfo(name = "schedule")
    val schedule: String?, // JSON строка
    @ColumnInfo(name = "employment")
    val employment: String?, // JSON строка
    @ColumnInfo(name = "employer")
    val employer: String?, // JSON строка
    @ColumnInfo(name = "area")
    val area: String?, // JSON строка
    @ColumnInfo(name = "skills")
    val skills: List<String>,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "industry")
    val industry: String?, // JSON строка
    @ColumnInfo(name = "published")
    val published: String?
)
