package ru.practicum.android.diploma.presentation.favorites.state

import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

sealed class FavoritesState {
    object Loading : FavoritesState()
    data class Success(val vacancies: List<VacancyDetails>) : FavoritesState()
    object Empty : FavoritesState()
    data class Error(val message: String) : FavoritesState()
}
