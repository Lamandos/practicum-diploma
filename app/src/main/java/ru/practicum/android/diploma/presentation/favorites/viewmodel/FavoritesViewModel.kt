package ru.practicum.android.diploma.presentation.details

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactors.FavoritesInteractor
import ru.practicum.android.diploma.domain.interactors.VacancyInteractor
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import ru.practicum.android.diploma.util.networkutils.NetworkUtils
import java.io.IOException
import java.net.UnknownHostException

private const val TAG = "VacancyViewModel"

class VacancyViewModel(
    private val vacancyInteractor: VacancyInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val context: Context
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

    private val _isVacancyDeleted = MutableLiveData<Boolean>(false)
    val isVacancyDeleted: LiveData<Boolean> = _isVacancyDeleted

    companion object {
        private const val ERROR_NETWORK = "Ошибка сети"
        private const val ERROR_NO_INTERNET = "Нет подключения к интернету"
        private const val ERROR_ACCESS = "Ошибка доступа к данным"
        private const val ERROR_STATE = "Ошибка состояния приложения"
        private const val ERROR_FAVORITES_NETWORK = "Ошибка сети при работе с избранным"
        private const val ERROR_LOAD_VACANCY = "Не удалось загрузить данные вакансии"
        private const val ERROR_REMOVE_FAVORITE = "Ошибка при удалении из избранного"
        private const val ERROR_ADD_FAVORITE = "Ошибка при добавлении в избранное"
        private const val ERROR_VACANCY_NOT_FOUND = "Вакансия не найдена на сервере"
    }

    fun init(vacancyId: String, fromFavorites: Boolean = false) {
        this.currentVacancyId = vacancyId
        this.isFromFavorites = fromFavorites
        _isVacancyDeleted.value = false
        loadVacancy()
    }

    private fun loadVacancy() {
        val vacancyId = currentVacancyId ?: return

        _isLoading.value = true
        _error.value = null
        _isVacancyDeleted.value = false

        viewModelScope.launch {
            try {
                if (isFromFavorites) {
                    // Сначала пытаемся получить из избранного
                    val favoriteVacancy = favoritesInteractor.getVacancyById(vacancyId)

                    if (favoriteVacancy != null) {
                        _vacancyDetails.value = favoriteVacancy
                        _isFavorite.value = true

                        // Если есть интернет, пытаемся обновить данные с сервера
                        if (NetworkUtils.isInternetAvailable(context)) {
                            try {
                                val serverVacancy = vacancyInteractor.getVacancyDetails(vacancyId)
                                // Если вакансия найдена на сервере - обновляем данные
                                _vacancyDetails.value = serverVacancy
                                serverVacancy?.let {
                                    favoritesInteractor.updateFavorite(it)
                                }
                            } catch (e: IOException) {
                                // Если вакансии нет на сервере из-за сетевой ошибки
                                _isVacancyDeleted.value = true
                                Log.w(TAG, "Network error while checking vacancy on server: ${e.message}")
                            } catch (e: UnknownHostException) {
                                // Если вакансии нет на сервере из-за проблем с хостом
                                _isVacancyDeleted.value = true
                                Log.w(TAG, "Host error while checking vacancy on server: ${e.message}")
                            } catch (e: SecurityException) {
                                // Если вакансии нет на сервере из-за проблем с доступом
                                _isVacancyDeleted.value = true
                                Log.w(TAG, "Security error while checking vacancy on server: ${e.message}")
                            } catch (e: IllegalStateException) {
                                // Если вакансии нет на сервере из-за проблем с состоянием
                                _isVacancyDeleted.value = true
                                Log.w(TAG, "Illegal state while checking vacancy on server: ${e.message}")
                            }
                        }
                    } else {
                        // Вакансия не найдена в избранном
                        _vacancyDetails.value = null
                        _isFavorite.value = false
                    }
                } else {
                    // Обычная загрузка вакансии (не из избранного)
                    val vacancy = vacancyInteractor.getVacancyDetails(vacancyId)
                    _vacancyDetails.value = vacancy
                    _isFavorite.value = favoritesInteractor.isFavorite(vacancyId)
                }
            } catch (e: IOException) {
                handleErrorWithLog("$ERROR_NETWORK: ${e.message}", "loadVacancy - IOException", e)
            } catch (e: UnknownHostException) {
                // При отсутствии интернета и fromFavorites = true не считаем это ошибкой
                if (!isFromFavorites) {
                    handleErrorWithLog(ERROR_NO_INTERNET, "loadVacancy - UnknownHostException", e)
                }
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
        val result = runCatching {
            favoritesInteractor.removeFromFavorites(vacancyId)
            _isFavorite.value = false
        }

        result.onFailure { throwable ->
            when (throwable) {
                is IOException, is SecurityException, is IllegalStateException -> {
                    handleErrorWithLog(
                        message = ERROR_REMOVE_FAVORITE,
                        operation = "removeFromFavorites - ${throwable::class.simpleName}",
                        exception = throwable
                    )
                }
            }
        }

        result.getOrThrow()
    }

    private suspend fun addToFavorites(vacancyId: String) {
        val result = runCatching {
            val currentVacancy = getCurrentVacancy(vacancyId)

            if (currentVacancy != null) {
                val vacancyToSave = getVacancyWithLogo(currentVacancy, vacancyId)
                favoritesInteractor.addToFavorites(vacancyToSave)
                _isFavorite.value = true
                updateVacancyDetailsIfNeeded(vacancyToSave)
            } else {
                handleError(ERROR_LOAD_VACANCY)
            }
        }

        result.onFailure { throwable ->
            when (throwable) {
                is IOException, is SecurityException, is IllegalStateException -> {
                    handleErrorWithLog(
                        message = ERROR_ADD_FAVORITE,
                        operation = "addToFavorites - ${throwable::class.simpleName}",
                        exception = throwable
                    )
                }
            }
        }

        result.getOrThrow()
    }

    private suspend fun getVacancyWithLogo(currentVacancy: VacancyDetails, vacancyId: String): VacancyDetails {
        val result = when {
            !currentVacancy.employer?.logo.isNullOrEmpty() || isFromFavorites -> currentVacancy
            !NetworkUtils.isInternetAvailable(context) -> {
                Log.w(TAG, "No internet available - skipping logo download")
                currentVacancy
            }

            else -> tryDownloadLogo(currentVacancy, vacancyId)
        }
        return result
    }

    private suspend fun tryDownloadLogo(currentVacancy: VacancyDetails, vacancyId: String): VacancyDetails {
        return try {
            val vacancyWithLogo = vacancyInteractor.getVacancyDetails(vacancyId)

            when {
                !vacancyWithLogo?.employer?.logo.isNullOrEmpty() -> vacancyWithLogo ?: currentVacancy
                else -> currentVacancy
            }
        } catch (e: IOException) {
            Log.w(TAG, "Network error while fetching logo: ${e.message}")
            currentVacancy
        } catch (e: UnknownHostException) {
            Log.w(TAG, "No internet while fetching logo: ${e.message}")
            currentVacancy
        } catch (e: SecurityException) {
            Log.w(TAG, "Security error while fetching logo: ${e.message}")
            currentVacancy
        } catch (e: IllegalStateException) {
            Log.w(TAG, "Illegal state while fetching logo: ${e.message}")
            currentVacancy
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

    private fun handleErrorWithLog(message: String, operation: String, exception: Throwable) {
        Log.e(TAG, "Error in $operation: ${exception.message}", exception)
        handleError(message)
    }
}
