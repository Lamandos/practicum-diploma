package ru.practicum.android.diploma.presentation.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactor.VacancyInteractor
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

class VacancyViewModel(
    private val interactor: VacancyInteractor
) : ViewModel() {

    private val _vacancyList = MutableLiveData<List<VacancyDetails>>()
    val vacancyList: LiveData<List<VacancyDetails>> get() = _vacancyList

    private val _vacancyDetails = MutableLiveData<VacancyDetails?>()
    val vacancyDetails: LiveData<VacancyDetails?> get() = _vacancyDetails

    fun loadVacancyDetails(id: String) {
        viewModelScope.launch {
            val result = interactor.getVacancyDetails(id)
            _vacancyDetails.postValue(result.getOrNull())
        }
    }

    fun searchVacancies(query: String) {
        viewModelScope.launch {
            val result = interactor.searchVacancies(query)
            _vacancyList.postValue(result.getOrDefault(emptyList()))
        }
    }
}
