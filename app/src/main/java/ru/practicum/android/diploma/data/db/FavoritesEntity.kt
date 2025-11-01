package ru.practicum.android.diploma.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "favorites")
@TypeConverters(ListConverter::class)
data class FavoritesEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "salary_from")
    val salaryFrom: Int?,
    @ColumnInfo(name = "salary_to")
    val salaryTo: Int?,
    @ColumnInfo(name = "salary_currency")
    val salaryCurrency: String?,
    @ColumnInfo(name = "address")
    val address: String?,
    @ColumnInfo(name = "experience_id")
    val experienceId: String,
    @ColumnInfo(name = "experience_name")
    val experienceName: String,
    @ColumnInfo(name = "schedule_id")
    val scheduleId: String,
    @ColumnInfo(name = "schedule_name")
    val scheduleName: String,
    @ColumnInfo(name = "employment_id")
    val employmentId: String,
    @ColumnInfo(name = "employment_name")
    val employmentName: String,
    @ColumnInfo(name = "employer_id")
    val employerId: String,
    @ColumnInfo(name = "employer_name")
    val employerName: String,
    @ColumnInfo(name = "employer_logo")
    val employerLogo: String?,
    @ColumnInfo(name = "area_id")
    val areaId: String,
    @ColumnInfo(name = "area_name")
    val areaName: String,
    @ColumnInfo(name = "skills")
    val skills: List<String>,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "industry_id")
    val industryId: String,
    @ColumnInfo(name = "industry_name")
    val industryName: String,
)
