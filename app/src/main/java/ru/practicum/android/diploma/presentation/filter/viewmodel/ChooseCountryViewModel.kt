package ru.practicum.android.diploma.presentation.filter.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
                _countries.value = emptyList()
                _error.value = Error.NoNetwork
            } else {
                try {
                    val result = countriesInteractor.getCountries()
                    _countries.value = result
                    _error.value = if (result.isEmpty()) Error.Other else null
                } catch (e: IOException) {
                    _countries.value = emptyList()
                    _error.value = Error.NoNetwork
                } catch (e: HttpException) {
                    _countries.value = emptyList()
                    _error.value = Error.ServerError
                } catch (e: Exception) {
                    _countries.value = emptyList()
                    _error.value = Error.Other
                }
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
