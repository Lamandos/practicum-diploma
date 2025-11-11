package ru.practicum.android.diploma.presentation.filter.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.practicum.android.diploma.domain.interactors.impl.CountriesInteractor
import ru.practicum.android.diploma.domain.models.vacancy.Country
import java.io.IOException

class ChooseCountryViewModel(
    private val countriesInteractor: CountriesInteractor,
    private val context: Context
) : ViewModel() {

    sealed class Error {
        object NoNetwork : Error()
        object ServerError : Error()
        object Other : Error()
    }

    private val _countries = MutableLiveData<List<Country>>(emptyList())
    val countries: LiveData<List<Country>> get() = _countries

    private val _error = MutableLiveData<Error?>()
    val error: LiveData<Error?> get() = _error

    fun loadCountries() {
        viewModelScope.launch {
            if (!isNetworkAvailable()) {
                handleError(Error.NoNetwork)
            } else {
                runCatching { countriesInteractor.getCountries() }
                    .onSuccess { result ->
                        _countries.value = result
                        _error.value = if (result.isEmpty()) Error.Other else null
                    }
                    .onFailure { e ->
                        when (e) {
                            is IOException -> handleError(Error.NoNetwork)
                            is HttpException -> handleError(Error.ServerError)
                            else -> {
                                handleError(Error.Other)
                                Log.e("ChooseCountryViewModel", "Unexpected error", e)
                            }
                        }
                    }
            }
        }
    }

    private fun handleError(errorType: Error) {
        _countries.value = emptyList()
        _error.value = errorType
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val capabilities = network?.let { cm.getNetworkCapabilities(it) }
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
