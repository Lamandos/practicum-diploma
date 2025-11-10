package ru.practicum.android.diploma.presentation.filter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.usecases.FilterInteractor
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters

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
            val currentFilters = filterInteractor.getFilters()
            _filtersState.value = currentFilters
            _isFilterApplied.value = isAnyFilterApplied(currentFilters)
        }
    }

    fun updateFilters(newFilters: VacancyFilters) {
        viewModelScope.launch {
            filterInteractor.saveFilters(newFilters)
            _filtersState.value = newFilters
            _isFilterApplied.value = isAnyFilterApplied(newFilters)
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

    private fun isAnyFilterApplied(filters: VacancyFilters): Boolean {
        return filters.region != null ||
            filters.industry != null ||
            filters.salary != null ||
            filters.hideWithoutSalary
    }
}
