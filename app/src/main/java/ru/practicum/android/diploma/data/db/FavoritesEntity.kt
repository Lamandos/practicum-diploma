package ru.practicum.android.diploma.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "favorites")
@TypeConverters(ListConverter::class)
data class FavoritesEntity(
    @PrimaryKey
    var id: String,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name = "salary_from")
    var salaryFrom: Int?,
    @ColumnInfo(name = "salary_to")
    var salaryTo: Int?,
    @ColumnInfo(name = "salary_currency")
    var salaryCurrency: String?,
    @ColumnInfo(name = "address")
    var address: String?,
    @ColumnInfo(name = "experience_id")
    var experienceId: String,
    @ColumnInfo(name = "experience_name")
    var experienceName: String,
    @ColumnInfo(name = "schedule_id")
    var scheduleId: String,
    @ColumnInfo(name = "schedule_name")
    var scheduleName: String,
    @ColumnInfo(name = "employment_id")
    var employmentId: String,
    @ColumnInfo(name = "employment_name")
    var employmentName: String,
    @ColumnInfo(name = "employer_id")
    var employerId: String,
    @ColumnInfo(name = "employer_name")
    var employerName: String,
    @ColumnInfo(name = "employer_logo")
    var employerLogo: String?,
    @ColumnInfo(name = "area_id")
    var areaId: String,
    @ColumnInfo(name = "area_name")
    var areaName: String,
    @ColumnInfo(name = "skills")
    var skills: List<String>,
    @ColumnInfo(name = "url")
    var url: String,
    @ColumnInfo(name = "industry_id")
    var industryId: String,
    @ColumnInfo(name = "industry_name")
    var industryName: String
)
