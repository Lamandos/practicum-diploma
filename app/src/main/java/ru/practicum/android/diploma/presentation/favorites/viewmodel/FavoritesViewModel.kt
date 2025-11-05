package ru.practicum.android.diploma.presentation.favorites.viewmodel

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
                _favoritesState.value = FavoritesState.Error("Ошибка сети при загрузке избранных вакансий")
            } catch (e: SQLException) {
                _favoritesState.value = FavoritesState.Error("Ошибка базы данных при загрузке избранных вакансий")
            } catch (e: IllegalStateException) {
                _favoritesState.value = FavoritesState.Error("Ошибка состояния приложения при загрузке избранных вакансий")
            } catch (e: SecurityException) {
                _favoritesState.value = FavoritesState.Error("Ошибка доступа к данным избранных вакансий")
            }
        }
    }

    fun refreshFavorites() {
        loadFavorites()
    }
}
