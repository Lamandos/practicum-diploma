package ru.practicum.android.diploma.presentation.filter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.usecases.FilterInteractor
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters
import ru.practicum.android.diploma.domain.models.filtermodels.isAnyFilterApplied

class FilterViewModel(
    private val filterInteractor: FilterInteractor
) : ViewModel() {

    private val _filtersState = MutableLiveData<VacancyFilters>()
    val filtersState: LiveData<VacancyFilters> = _filtersState

    private val _isFilterApplied = MutableLiveData<Boolean>()
    val isFilterApplied: LiveData<Boolean> = _isFilterApplied

    init {
        loadCurrentFilters()
    }

    fun loadCurrentFilters() {
        viewModelScope.launch {
            val filters = filterInteractor.getFilters()
            _filtersState.value = filters
            _isFilterApplied.value = filters.isAnyFilterApplied()
        }
    }

    fun updateFilters(newFilters: VacancyFilters) {
        viewModelScope.launch {
            filterInteractor.saveFilters(newFilters)
            _filtersState.value = newFilters
            _isFilterApplied.value = newFilters.isAnyFilterApplied()
        }
    }

    fun clearFilters() {
        viewModelScope.launch {
            val emptyFilters = VacancyFilters()
            filterInteractor.saveFilters(emptyFilters)
            _filtersState.value = emptyFilters
            _isFilterApplied.value = false
        }
    }
}
