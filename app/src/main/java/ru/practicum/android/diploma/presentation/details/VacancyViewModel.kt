package ru.practicum.android.diploma.presentation.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactors.FavoritesInteractor
import ru.practicum.android.diploma.domain.interactors.VacancyInteractor
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import java.io.IOException
import java.net.UnknownHostException

class VacancyViewModel(
    private val vacancyInteractor: VacancyInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _vacancyDetails = MutableLiveData<VacancyDetails?>()
    val vacancyDetails: LiveData<VacancyDetails?> = _vacancyDetails

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentVacancyId: String? = null
    private var isFromFavorites: Boolean = false

    fun init(vacancyId: String, fromFavorites: Boolean = false) {
        this.currentVacancyId = vacancyId
        this.isFromFavorites = fromFavorites
        loadVacancy()
    }

    private fun loadVacancy() {
        val vacancyId = currentVacancyId ?: return

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                if (isFromFavorites) {
                    val vacancy = favoritesInteractor.getVacancyById(vacancyId)
                    _vacancyDetails.value = vacancy
                    _isFavorite.value = vacancy != null
                } else {
                    val vacancy = vacancyInteractor.getVacancyDetails(vacancyId)
                    _vacancyDetails.value = vacancy
                    _isFavorite.value = favoritesInteractor.isFavorite(vacancyId)
                }
            } catch (e: IOException) {
                handleError("Ошибка сети: ${e.message}")
            } catch (e: UnknownHostException) {
                handleError("Нет подключения к интернету")
            } catch (e: SecurityException) {
                handleError("Ошибка доступа к данным")
            } catch (e: IllegalStateException) {
                handleError("Ошибка состояния приложения")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onFavoritesClicked() {
        val vacancyId = currentVacancyId ?: return

        viewModelScope.launch {
            try {
                val isCurrentlyFavorite = _isFavorite.value ?: false

                if (isCurrentlyFavorite) {
                    removeFromFavorites(vacancyId)
                } else {
                    addToFavorites(vacancyId)
                }
            } catch (e: IOException) {
                handleError("Ошибка сети при работе с избранным")
            } catch (e: SecurityException) {
                handleError("Ошибка доступа к данным")
            } catch (e: IllegalStateException) {
                handleError("Ошибка состояния приложения")
            }
        }
    }

    private suspend fun removeFromFavorites(vacancyId: String) {
        try {
            favoritesInteractor.removeFromFavorites(vacancyId)
            _isFavorite.value = false
        } catch (e: Exception) {
            handleError("Не удалось удалить из избранного")
            throw e // Пробрасываем исключение дальше
        }
    }

    private suspend fun addToFavorites(vacancyId: String) {
        try {
            val currentVacancy = getCurrentVacancy(vacancyId)

            if (currentVacancy != null) {
                favoritesInteractor.addToFavorites(currentVacancy)
                _isFavorite.value = true
                updateVacancyDetailsIfNeeded(currentVacancy)
            } else {
                handleError("Не удалось загрузить данные вакансии")
            }
        } catch (e: Exception) {
            handleError("Не удалось добавить в избранное")
            throw e // Пробрасываем исключение дальше
        }
    }

    private suspend fun getCurrentVacancy(vacancyId: String): VacancyDetails? {
        return _vacancyDetails.value ?: vacancyInteractor.getVacancyDetails(vacancyId)
    }

    private fun updateVacancyDetailsIfNeeded(vacancy: VacancyDetails) {
        if (_vacancyDetails.value == null) {
            _vacancyDetails.value = vacancy
        }
    }

    private fun handleError(message: String) {
        _error.value = message
    }
}
