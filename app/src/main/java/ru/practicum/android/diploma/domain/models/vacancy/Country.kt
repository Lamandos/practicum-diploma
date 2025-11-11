package ru.practicum.android.diploma.domain.models.vacancy

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Country(
    val id: Int,
    val name: String
) : Parcelable
