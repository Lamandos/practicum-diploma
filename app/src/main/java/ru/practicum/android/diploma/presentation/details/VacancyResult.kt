package ru.practicum.android.diploma.presentation.details

sealed class VacancyResult<out T> {
    data class Success<out T>(val data: T) : VacancyResult<T>()
    data class Error(val code: Int) : VacancyResult<Nothing>()
}
