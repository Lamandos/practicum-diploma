package ru.practicum.android.diploma.presentation.filter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto
import ru.practicum.android.diploma.domain.interactors.IndustryInteractor
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ChooseIndustryViewModel(
    private val industryInteractor: IndustryInteractor
) : ViewModel() {

    private val _industriesState = MutableLiveData<List<FilterIndustryDto>>()
    val industriesState: LiveData<List<FilterIndustryDto>> = _industriesState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var allIndustries: List<FilterIndustryDto> = emptyList()

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
                    allIndustries = industries
                    _industriesState.value = industries
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
            _industriesState.value = allIndustries
        } else {
            viewModelScope.launch {
                try {
                    val filteredIndustries = industryInteractor.searchIndustries(query)
                    _industriesState.value = filteredIndustries ?: emptyList()
                } catch (e: IOException) {
                    // При ошибке сети при поиске показываем пустой список
                    _industriesState.value = emptyList()
                    e.printStackTrace() // Логируем для отладки
                } catch (e: IllegalStateException) {
                    // При ошибке состояния при поиске показываем пустой список
                    _industriesState.value = emptyList()
                    e.printStackTrace() // Логируем для отладки
                }
            }
        }
    }

    fun resetSearch() {
        _industriesState.value = allIndustries
    }

    private fun handleNetworkError(message: String, exception: Exception) {
        _isError.value = true
        _errorMessage.value = message
        exception.printStackTrace() // Логируем для отладки
    }
}
