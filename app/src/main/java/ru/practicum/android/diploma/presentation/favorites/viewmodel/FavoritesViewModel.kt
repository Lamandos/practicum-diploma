package ru.practicum.android.diploma.presentation.favorites.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactors.FavoritesInteractor
import ru.practicum.android.diploma.presentation.favorites.state.FavoritesState

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _favoritesState = MutableLiveData<FavoritesState>(FavoritesState.Loading)
    val favoritesState: LiveData<FavoritesState> get() = _favoritesState

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        _favoritesState.value = FavoritesState.Loading

        viewModelScope.launch {
            try {
                favoritesInteractor.getAllFavorites().collect { vacancies ->
                    if (vacancies.isEmpty()) {
                        _favoritesState.value = FavoritesState.Empty
                    } else {
                        _favoritesState.value = FavoritesState.Success(vacancies)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _favoritesState.value = FavoritesState.Error("Не удалось загрузить избранные вакансии: ${e.message}")
            }
        }
    }

    fun refreshFavorites() {
        loadFavorites()
    }
}
