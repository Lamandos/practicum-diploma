package ru.practicum.android.diploma.presentation.filter.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.practicum.android.diploma.data.dto.filterdto.FilterAreaDto
import ru.practicum.android.diploma.data.repositories.AreasRepository
import java.io.IOException

class ChooseRegionViewModel(
    private val repository: AreasRepository
) : ViewModel() {

    private val _allRegions = MutableLiveData<List<FilterAreaDto>>(emptyList())
    val allRegions: LiveData<List<FilterAreaDto>> get() = _allRegions

    private val _filteredRegions = MutableLiveData<List<FilterAreaDto>>(emptyList())
    val filteredRegions: LiveData<List<FilterAreaDto>> get() = _filteredRegions

    private var fullRegionList: List<FilterAreaDto> = emptyList()

    fun loadRegions(countryId: Int? = null) {
        viewModelScope.launch {
            fullRegionList = emptyList()
            val regions = try {
                if (countryId != null) {
                    repository.getRegionsByCountry(countryId).orEmpty()
                } else {
                    repository.getAllRegions().orEmpty()
                }
            } catch (e: IOException) {
                Log.e("ChooseRegionViewModel", "Network error", e)
                emptyList()
            } catch (e: HttpException) {
                Log.e("ChooseRegionViewModel", "HTTP error", e)
                emptyList()
            }

            fullRegionList = regions
            _allRegions.value = regions
            _filteredRegions.value = regions
        }
    }

    fun filterRegions(query: String) {
        val filtered = if (query.isBlank()) {
            fullRegionList
        } else {
            fullRegionList.filter { it.name.contains(query, ignoreCase = true) }
        }

        _filteredRegions.value = filtered
    }
}
