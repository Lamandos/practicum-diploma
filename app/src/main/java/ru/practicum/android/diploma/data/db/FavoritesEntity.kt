package ru.practicum.android.diploma.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoritesEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "salary")
    val salary: String?,
    @ColumnInfo(name = "address")
    val address: String?,
    @ColumnInfo(name = "experience")
    val experience: String?,
    @ColumnInfo(name = "schedule")
    val schedule: String?,
    @ColumnInfo(name = "employment")
    val employment: String?,
    @ColumnInfo(name = "employer")
    val employer: String?,
    @ColumnInfo(name = "area")
    val area: String?, //
    @ColumnInfo(name = "skills")
    val skills: List<String>,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "industry")
    val industry: String?,
    @ColumnInfo(name = "published")
    val published: String?
)
