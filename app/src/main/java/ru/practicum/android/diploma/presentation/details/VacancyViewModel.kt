package ru.practicum.android.diploma.presentation.details

import android.util.Log
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

private const val TAG = "VacancyViewModel"

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

    // Константы для сообщений об ошибках
    companion object {
        private const val ERROR_NETWORK = "Ошибка сети"
        private const val ERROR_NO_INTERNET = "Нет подключения к интернету"
        private const val ERROR_ACCESS = "Ошибка доступа к данным"
        private const val ERROR_STATE = "Ошибка состояния приложения"
        private const val ERROR_FAVORITES_NETWORK = "Ошибка сети при работе с избранным"
        private const val ERROR_LOAD_VACANCY = "Не удалось загрузить данные вакансии"
        private const val ERROR_REMOVE_FAVORITE = "Ошибка при удалении из избранного"
        private const val ERROR_ADD_FAVORITE = "Ошибка при добавлении в избранное"
    }

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
                handleErrorWithLog("$ERROR_NETWORK: ${e.message}", "loadVacancy - IOException", e)
            } catch (e: UnknownHostException) {
                handleErrorWithLog(ERROR_NO_INTERNET, "loadVacancy - UnknownHostException", e)
            } catch (e: SecurityException) {
                handleErrorWithLog(ERROR_ACCESS, "loadVacancy - SecurityException", e)
            } catch (e: IllegalStateException) {
                handleErrorWithLog(ERROR_STATE, "loadVacancy - IllegalStateException", e)
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
                handleErrorWithLog(ERROR_FAVORITES_NETWORK, "onFavoritesClicked - IOException", e)
            } catch (e: SecurityException) {
                handleErrorWithLog(ERROR_ACCESS, "onFavoritesClicked - SecurityException", e)
            } catch (e: IllegalStateException) {
                handleErrorWithLog(ERROR_STATE, "onFavoritesClicked - IllegalStateException", e)
            }
        }
    }

    private suspend fun removeFromFavorites(vacancyId: String) {
        try {
            favoritesInteractor.removeFromFavorites(vacancyId)
            _isFavorite.value = false
        } catch (e: Exception) {
            when (e) {
                is IOException, is SecurityException, is IllegalStateException -> {
                    handleErrorWithLog(ERROR_REMOVE_FAVORITE, "removeFromFavorites - ${e::class.simpleName}", e)
                }
            }
            throw e
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
                handleError(ERROR_LOAD_VACANCY)
            }
        } catch (e: Exception) {
            when (e) {
                is IOException, is SecurityException, is IllegalStateException -> {
                    handleErrorWithLog(ERROR_ADD_FAVORITE, "addToFavorites - ${e::class.simpleName}", e)
                }
            }
            throw e
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

    private fun handleErrorWithLog(message: String, operation: String, exception: Exception) {
        Log.e(TAG, "Error in $operation: ${exception.message}", exception)
        handleError(message)
    }
}
