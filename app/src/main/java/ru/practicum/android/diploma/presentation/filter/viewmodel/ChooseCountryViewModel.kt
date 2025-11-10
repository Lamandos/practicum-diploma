package ru.practicum.android.diploma.presentation.filter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactors.impl.CountriesInteractor
import ru.practicum.android.diploma.domain.models.vacancy.Country

class ChooseCountryViewModel(
    private val countriesInteractor: CountriesInteractor
) : ViewModel() {

    private val _countries = MutableLiveData<List<Country>>(emptyList())
    val countries: LiveData<List<Country>> get() = _countries

    private val _error = MutableLiveData<Boolean>(false)
    val error: LiveData<Boolean> get() = _error

    fun loadCountries() {
        viewModelScope.launch {
            val result = runCatching { countriesInteractor.getCountries() }
                .getOrElse { emptyList() }

            _countries.value = result
            _error.value = result.isEmpty()
        }
    }
}
