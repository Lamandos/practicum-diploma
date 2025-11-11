package ru.practicum.android.diploma.domain.models.filtermodels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.practicum.android.diploma.domain.models.vacancy.Country

@Parcelize
data class Region(
    val id: Int,
    val name: String,
    val country: Country?
) : Parcelable
