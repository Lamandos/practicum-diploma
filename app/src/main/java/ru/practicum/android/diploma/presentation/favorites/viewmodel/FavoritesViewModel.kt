package ru.practicum.android.diploma.presentation.favorites.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactors.FavoritesInteractor
import ru.practicum.android.diploma.presentation.favorites.state.FavoritesState
import java.io.IOException
import java.sql.SQLException

private const val TAG = "FavoritesViewModel"

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
            } catch (e: IOException) {
                Log.e(TAG, "Network error loading favorites", e)
                _favoritesState.value = FavoritesState.Error("Ошибка сети")
            } catch (e: SQLException) {
                Log.e(TAG, "Database error loading favorites", e)
                _favoritesState.value = FavoritesState.Error("Ошибка базы данных")
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Illegal state loading favorites", e)
                _favoritesState.value = FavoritesState.Error("Ошибка состояния")
            } catch (e: SecurityException) {
                Log.e(TAG, "Security error loading favorites", e)
                _favoritesState.value = FavoritesState.Error("Ошибка доступа")
            }
        }
    }

    fun refreshFavorites() {
        loadFavorites()
    }
}
