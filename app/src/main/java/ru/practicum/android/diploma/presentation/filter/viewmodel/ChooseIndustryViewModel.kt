package ru.practicum.android.diploma.presentation.filter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto
import ru.practicum.android.diploma.domain.interactors.IndustryInteractor

class ChooseIndustryViewModel(
    private val industryInteractor: IndustryInteractor
) : ViewModel() {

    private val _industriesState = MutableLiveData<List<FilterIndustryDto>>()
    val industriesState: LiveData<List<FilterIndustryDto>> = _industriesState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private var allIndustries: List<FilterIndustryDto> = emptyList()

    init {
        loadIndustries()
    }

    fun loadIndustries() {
        _isLoading.value = true
        _isError.value = false

        viewModelScope.launch {
            try {
                val industries = industryInteractor.getAllIndustries()
                if (industries != null) {
                    allIndustries = industries
                    _industriesState.value = industries
                    _isError.value = false
                } else {
                    _isError.value = true
                }
            } catch (e: Exception) {
                _isError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchIndustries(query: String) {
        if (query.isBlank()) {
            _industriesState.value = allIndustries
        } else {
            viewModelScope.launch {
                val filteredIndustries = industryInteractor.searchIndustries(query)
                _industriesState.value = filteredIndustries ?: emptyList()
            }
        }
    }

    fun resetSearch() {
        _industriesState.value = allIndustries
    }
}
