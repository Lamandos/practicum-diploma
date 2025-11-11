package ru.practicum.android.diploma.presentation.filter.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactors.impl.RegionsInteractor
import ru.practicum.android.diploma.domain.models.filtermodels.Region
import ru.practicum.android.diploma.domain.models.vacancy.Country

class ChooseRegionViewModel(
    private val regionsInteractor: RegionsInteractor,
    private val context: Context
) : ViewModel() {

    enum class RegionError {
        NO_RESULTS,
        LOAD_FAILED
    }

    private val _allRegions = MutableLiveData<List<Region>>(emptyList())
    val allRegions: LiveData<List<Region>> get() = _allRegions

    private val _filteredRegions = MutableLiveData<List<Region>>(emptyList())
    val filteredRegions: LiveData<List<Region>> get() = _filteredRegions

    private val _error = MutableLiveData<RegionError?>(null)
    val error: LiveData<RegionError?> get() = _error

    private var fullRegionList: List<Region> = emptyList()

    fun loadRegions(country: Country) {
        viewModelScope.launch {
            if (!isNetworkAvailable()) {
                _allRegions.value = emptyList()
                _filteredRegions.value = emptyList()
                _error.value = RegionError.LOAD_FAILED
                return@launch
            }

            runCatching {
                regionsInteractor.getRegions(country)
            }.onSuccess { regions ->
                fullRegionList = regions
                _allRegions.value = regions
                _filteredRegions.value = regions
                _error.value = if (regions.isEmpty()) RegionError.NO_RESULTS else null
            }.onFailure {
                _allRegions.value = emptyList()
                _filteredRegions.value = emptyList()
                _error.value = RegionError.LOAD_FAILED
            }
        }
    }

    fun loadAllRegions() {
        viewModelScope.launch {
            if (!isNetworkAvailable()) {
                _allRegions.value = emptyList()
                _filteredRegions.value = emptyList()
                _error.value = RegionError.LOAD_FAILED
                return@launch
            }

            runCatching {
                regionsInteractor.getAllRegions()
            }.onSuccess { regions ->
                fullRegionList = regions
                _allRegions.value = regions
                _filteredRegions.value = regions
                _error.value = if (regions.isEmpty()) RegionError.NO_RESULTS else null
            }.onFailure {
                _allRegions.value = emptyList()
                _filteredRegions.value = emptyList()
                _error.value = RegionError.LOAD_FAILED
            }
        }
    }

    fun filterRegions(query: String) {
        val filtered = if (query.isBlank()) {
            fullRegionList
        } else {
            fullRegionList.filter { it.name.contains(query, ignoreCase = true) }
        }

        _filteredRegions.value = filtered
        _error.value = if (query.isNotBlank() && filtered.isEmpty()) RegionError.NO_RESULTS else null
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnectedOrConnecting == true
    }
}
