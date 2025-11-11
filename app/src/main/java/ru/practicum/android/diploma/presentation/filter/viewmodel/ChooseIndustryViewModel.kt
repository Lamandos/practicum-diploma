package ru.practicum.android.diploma.presentation.filter.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactors.IndustryInteractor
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.presentation.mappers.IndustryUiMapper
import ru.practicum.android.diploma.ui.model.FilterIndustryUI

class ChooseIndustryViewModel(
    private val industryInteractor: IndustryInteractor,
    private val context: Context
) : ViewModel() {

    sealed class IndustryError {
        object NoNetwork : IndustryError()
        object ServerError : IndustryError()
    }

    private val _industriesState = MutableLiveData<List<FilterIndustryUI>>(emptyList())
    val industriesState: LiveData<List<FilterIndustryUI>> = _industriesState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<IndustryError?>()
    val error: LiveData<IndustryError?> = _error

    private var allIndustriesDomain: List<FilterIndustry> = emptyList()

    init {
        loadIndustries()
    }

    fun loadIndustries() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            if (!isNetworkAvailable()) {
                handleLoadFailure(IndustryError.NoNetwork)
            } else {
                runCatching { industryInteractor.getAllIndustries() }
                    .onSuccess { industries ->
                        if (industries.isNullOrEmpty()) {
                            handleLoadFailure(IndustryError.ServerError)
                        } else {
                            allIndustriesDomain = industries
                            _industriesState.value = IndustryUiMapper.mapDomainListToUi(industries)
                            _error.value = null
                        }
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                        handleLoadFailure(IndustryError.ServerError)
                    }
            }

            _isLoading.value = false
        }
    }

    fun searchIndustries(query: String) {
        val filtered = if (query.isBlank()) {
            allIndustriesDomain
        } else {
            allIndustriesDomain.filter { it.name.contains(query, ignoreCase = true) }
        }
        _industriesState.value = IndustryUiMapper.mapDomainListToUi(filtered)
    }

    private fun handleLoadFailure(errorType: IndustryError) {
        _industriesState.value = emptyList()
        _error.value = errorType
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val capabilities = network?.let { cm.getNetworkCapabilities(it) }
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
