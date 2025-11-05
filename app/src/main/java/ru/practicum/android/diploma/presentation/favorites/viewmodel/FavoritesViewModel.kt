package ru.practicum.android.diploma.presentation.favorites.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
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
        println("DEBUG: FavoritesViewModel - loadFavorites started")
        _favoritesState.value = FavoritesState.Loading

        viewModelScope.launch {
            try {
                println("DEBUG: FavoritesViewModel - calling getAllFavorites")
                favoritesInteractor.getAllFavorites().collect { vacancies ->
                    println("DEBUG: FavoritesViewModel - collected ${vacancies.size} vacancies")
                    if (vacancies.isEmpty()) {
                        println("DEBUG: FavoritesViewModel - showing empty state")
                        _favoritesState.value = FavoritesState.Empty
                    } else {
                        println("DEBUG: FavoritesViewModel - showing success with ${vacancies.size} vacancies")
                        _favoritesState.value = FavoritesState.Success(vacancies)
                    }
                }
            } catch (e: Exception) {
                println("DEBUG: FavoritesViewModel - ERROR: ${e.message}")
                e.printStackTrace()
                _favoritesState.value = FavoritesState.Error("Не удалось загрузить избранные вакансии: ${e.message}")
            }
        }
    }

    fun refreshFavorites() {
        loadFavorites()
    }
}
