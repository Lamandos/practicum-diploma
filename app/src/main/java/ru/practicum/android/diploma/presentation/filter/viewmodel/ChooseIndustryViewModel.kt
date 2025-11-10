package ru.practicum.android.diploma.presentation.filter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactors.IndustryInteractor
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.presentation.mappers.IndustryUiMapper
import ru.practicum.android.diploma.ui.model.FilterIndustryUI
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ChooseIndustryViewModel(
    private val industryInteractor: IndustryInteractor
) : ViewModel() {

    private val _industriesState = MutableLiveData<List<FilterIndustryUI>>()
    val industriesState: LiveData<List<FilterIndustryUI>> = _industriesState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var allIndustriesDomain: List<FilterIndustry> = emptyList()

    init {
        loadIndustries()
    }

    fun loadIndustries() {
        _isLoading.value = true
        _isError.value = false
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val industries = industryInteractor.getAllIndustries()
                if (industries != null) {
                    allIndustriesDomain = industries
                    _industriesState.value = IndustryUiMapper.mapDomainListToUi(industries)
                    _isError.value = false
                } else {
                    _isError.value = true
                    _errorMessage.value = "Не удалось загрузить список отраслей"
                }
            } catch (e: IOException) {
                handleNetworkError("Ошибка сети при загрузке отраслей", e)
            } catch (e: SocketTimeoutException) {
                handleNetworkError("Превышено время ожидания сервера", e)
            } catch (e: UnknownHostException) {
                handleNetworkError("Нет подключения к интернету", e)
            } catch (e: IllegalStateException) {
                handleNetworkError("Ошибка состояния приложения", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchIndustries(query: String) {
        if (query.isBlank()) {
            _industriesState.value = IndustryUiMapper.mapDomainListToUi(allIndustriesDomain)
        } else {
            viewModelScope.launch {
                try {
                    val filteredIndustries = industryInteractor.searchIndustries(query)
                    _industriesState.value = IndustryUiMapper.mapDomainListToUi(filteredIndustries ?: emptyList())
                } catch (e: IOException) {
                    _industriesState.value = emptyList()
                    e.printStackTrace()
                } catch (e: IllegalStateException) {
                    _industriesState.value = emptyList()
                    e.printStackTrace()
                }
            }
        }
    }

    fun resetSearch() {
        _industriesState.value = IndustryUiMapper.mapDomainListToUi(allIndustriesDomain)
    }

    private fun handleNetworkError(message: String, exception: Exception) {
        _isError.value = true
        _errorMessage.value = message
        exception.printStackTrace()
    }
}
