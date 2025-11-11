package ru.practicum.android.diploma.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterIndustryUI(
    val id: Int,
    val name: String
) : Parcelable
