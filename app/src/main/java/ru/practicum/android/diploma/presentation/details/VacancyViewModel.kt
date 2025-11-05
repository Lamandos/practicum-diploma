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

    private val _vacancyList = MutableLiveData<List<VacancyDetails>>()
    val vacancyList: LiveData<List<VacancyDetails>> get() = _vacancyList

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
            println("DEBUG: VacancyViewModel - vacancyId is null")
            return
        }

        println("DEBUG: VacancyViewModel - onFavoritesClicked called for: $vacancyId")

        viewModelScope.launch {
            try {
                val isCurrentlyFavorite = _isFavorite.value ?: false
                println("DEBUG: VacancyViewModel - current favorite state: $isCurrentlyFavorite")

                if (isCurrentlyFavorite) {
                    println("DEBUG: VacancyViewModel - removing from favorites")
                    favoritesInteractor.removeFromFavorites(vacancyId)
                    _isFavorite.value = false
                    println("DEBUG: VacancyViewModel - removed from favorites")
                } else {
                    println("DEBUG: VacancyViewModel - adding to favorites")
                    var currentVacancy = _vacancyDetails.value

                    if (currentVacancy == null) {
                        println("DEBUG: VacancyViewModel - loading vacancy details")
                        currentVacancy = vacancyInteractor.getVacancyDetails(vacancyId)
                    }

                    if (currentVacancy != null) {
                        println("DEBUG: VacancyViewModel - adding vacancy: ${currentVacancy.id}")
                        favoritesInteractor.addToFavorites(currentVacancy)
                        _isFavorite.value = true
                        println("DEBUG: VacancyViewModel - added to favorites")

                        if (_vacancyDetails.value == null) {
                            _vacancyDetails.value = currentVacancy
                        }
                    } else {
                        println("DEBUG: VacancyViewModel - failed to load vacancy details")
                        _error.value = "Не удалось загрузить данные вакансии"
                    }
                }
            } catch (e: Exception) {
                println("DEBUG: VacancyViewModel - ERROR: ${e.message}")
                _error.value = "Ошибка: ${e.message}"
                e.printStackTrace()
            }
        }
    }
}
