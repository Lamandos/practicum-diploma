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

    companion object {
        const val ERROR_SERVER = "ERROR_SERVER"
        const val ERROR_VACANCY_NOT_FOUND = "ERROR_VACANCY_NOT_FOUND"
    }

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
        currentVacancyId = vacancyId
        isFromFavorites = fromFavorites
        loadVacancy()
    }

    private fun loadVacancy() {
        val vacancyId = currentVacancyId ?: return
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val vacancy = if (isFromFavorites) {
                    favoritesInteractor.getVacancyById(vacancyId)
                } else {
                    vacancyInteractor.getVacancyDetails(vacancyId)
                }

                if (vacancy != null) {
                    _vacancyDetails.value = vacancy
                    _isFavorite.value = favoritesInteractor.isFavorite(vacancy.id)
                } else {
                    _error.value = "ERROR_VACANCY_NOT_FOUND"
                }
            } catch (e: Exception) {
                _error.value = "ERROR_SERVER"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onFavoritesClicked() {
        val vacancy = _vacancyDetails.value ?: return

        viewModelScope.launch {
            try {
                favoritesInteractor.toggleFavorite(vacancy)
                _isFavorite.value = favoritesInteractor.isFavorite(vacancy.id)
            } catch (e: Exception) {
                _error.value = "ERROR_SERVER"
            }
        }
    }
}
