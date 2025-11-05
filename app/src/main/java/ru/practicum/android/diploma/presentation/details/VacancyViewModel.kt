// VacancyViewModel.kt
package ru.practicum.android.diploma.presentation.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactors.FavoritesInteractor
import ru.practicum.android.diploma.domain.interactors.VacancyInteractor
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

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
        val vacancyId = currentVacancyId ?: run {
            return
        }

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

                    val isFavorite = favoritesInteractor.isFavorite(vacancyId)
                    _isFavorite.value = isFavorite
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onFavoritesClicked() {
        val vacancyId = currentVacancyId ?: run {
            return
        }

        viewModelScope.launch {
            try {
                val isCurrentlyFavorite = _isFavorite.value ?: false

                if (isCurrentlyFavorite) {
                    // Удаляем из избранного
                    favoritesInteractor.removeFromFavorites(vacancyId)
                    _isFavorite.value = false
                } else {
                    // Добавляем в избранное
                    // Получаем текущие данные или загружаем заново
                    var currentVacancy = _vacancyDetails.value

                    // Если данных нет, пробуем загрузить
                    if (currentVacancy == null) {
                        currentVacancy = vacancyInteractor.getVacancyDetails(vacancyId)
                    }

                    if (currentVacancy != null) {
                        favoritesInteractor.addToFavorites(currentVacancy)
                        _isFavorite.value = true
                        // Обновляем данные если загрузили заново
                        if (_vacancyDetails.value == null) {
                            _vacancyDetails.value = currentVacancy
                        }
                    } else {
                        _error.value = "Не удалось загрузить данные вакансии"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
                e.printStackTrace()
            }
        }
    }
}
