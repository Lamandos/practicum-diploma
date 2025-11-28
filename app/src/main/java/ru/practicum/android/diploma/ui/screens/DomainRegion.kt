package ru.practicum.android.diploma.ui.screens

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainRegion(
    val name: String,
    val id: Int
) : Parcelable
