package ru.practicum.android.diploma.presentation.filter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactors.impl.CountriesInteractor
import ru.practicum.android.diploma.domain.interactors.impl.RegionsInteractor
import ru.practicum.android.diploma.domain.models.filtermodels.Region
import ru.practicum.android.diploma.domain.models.vacancy.Country

class ChooseWorkPlaceViewModel(
    private val countriesInteractor: CountriesInteractor,
    private val regionsInteractor: RegionsInteractor
) : ViewModel() {

    private val _countries = MutableLiveData<List<Country>>(emptyList())
    val countries: LiveData<List<Country>> get() = _countries

    private val _regions = MutableLiveData<List<Region>>(emptyList())
    val regions: LiveData<List<Region>> get() = _regions

    private val _filteredRegions = MutableLiveData<List<Region>>(emptyList())
    val filteredRegions: LiveData<List<Region>> get() = _filteredRegions

    private var fullRegionList: List<Region> = emptyList()

    private val _selectedCountry = MutableLiveData<Country?>()
    val selectedCountry: LiveData<Country?> get() = _selectedCountry

    private val _selectedRegion = MutableLiveData<Region?>()
    val selectedRegion: LiveData<Region?> get() = _selectedRegion

    fun loadCountries() = viewModelScope.launch {
        _countries.value = countriesInteractor.getCountries()
    }

    fun loadRegions(country: Country) = viewModelScope.launch {
        fullRegionList = regionsInteractor.getRegions(country)
        _regions.value = fullRegionList
        _filteredRegions.value = fullRegionList
    }

    fun filterRegions(query: String) {
        val filtered = if (query.isBlank()) {
            fullRegionList
        } else {
            fullRegionList.filter { it.name.contains(query, ignoreCase = true) }
        }
        _filteredRegions.value = filtered
    }

    fun selectCountry(country: Country?) {
        _selectedCountry.value = country
        _selectedRegion.value = null
    }

    fun selectRegion(region: Region?) {
        _selectedRegion.value = region
    }
}
