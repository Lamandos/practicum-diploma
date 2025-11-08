package ru.practicum.android.diploma.presentation.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactors.SearchVacanciesInteractor
import ru.practicum.android.diploma.domain.models.mappers.DomainMappers.toVacancy
import ru.practicum.android.diploma.domain.models.vacancy.Vacancy
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val vacancies: List<Vacancy>) : SearchState()
    object Empty : SearchState()
    data class Error(val throwable: Throwable? = null) : SearchState()
}

class SearchViewModel(
    private val interactor: SearchVacanciesInteractor
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>(SearchState.Idle)
    val searchState: LiveData<SearchState> get() = _searchState
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage
    private var isLoading = false
    private var isLoadingNextPageInternal = false
    private var currentPage = 0
    private var maxPages = Int.MAX_VALUE
    private var lastQuery: String = ""
    val totalFoundCount: Int
        get() = interactor.totalFoundCount
    private val _isErrorToastShown = MutableLiveData(false)
    val isErrorToastShown: LiveData<Boolean> get() = _isErrorToastShown
    private val _showLoadingState = MutableLiveData<Boolean>()
    val showLoadingState: LiveData<Boolean> get() = _showLoadingState
    val isLoadingNextPage: Boolean get() = isLoadingNextPageInternal

    fun setLoading() {
        _searchState.value = SearchState.Loading
    }

    fun searchVacancies(query: String) {
        if (isLoading) return
        lastQuery = query
        currentPage = 0
        maxPages = Int.MAX_VALUE
        isLoadingNextPageInternal = false
        _isErrorToastShown.value = false
        isLoading = true
        _searchState.value = SearchState.Loading

        viewModelScope.launch {
            try {
                val result = interactor.searchVacancies(query, currentPage + 1, PAGE_SIZE)
                if (result.isSuccess) {
                    val vacancies = result.getOrThrow()
                    handleSuccess(vacancies, append = false)
                } else {
                    _searchState.postValue(SearchState.Error(result.exceptionOrNull()))
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun loadNextPage() {
        if (isLoading || isLoadingNextPageInternal || currentPage >= maxPages) return
        isLoadingNextPageInternal = true
        _showLoadingState.postValue(true)

        viewModelScope.launch {
            try {
                val result = interactor.searchVacancies(lastQuery, currentPage + 1, PAGE_SIZE)
                if (result.isSuccess) {
                    val vacancies = result.getOrThrow()
                    handleSuccess(vacancies, append = true)
                } else {
                    if (_isErrorToastShown.value == false) {
                        _errorMessage.postValue("Проверьте подключение к интернету")
                        _isErrorToastShown.postValue(true)
                    }
                }
            } finally {
                isLoadingNextPageInternal = false
                _showLoadingState.postValue(false)
            }
        }
    }

    private fun handleSuccess(vacancies: List<VacancyDetails>, append: Boolean) {
        val newItems = vacancies.map { it.toVacancy() }
        currentPage++
        if (append) {
            _isErrorToastShown.value = false
            val current = (_searchState.value as? SearchState.Success)?.vacancies ?: emptyList()
            _searchState.postValue(SearchState.Success(current + newItems))
        } else {
            _searchState.postValue(
                if (newItems.isEmpty()) SearchState.Empty else SearchState.Success(newItems)
            )
        }
    }

    fun resetSearch() {
        currentPage = 0
        maxPages = Int.MAX_VALUE
        lastQuery = ""
        isLoadingNextPageInternal = false
        _searchState.value = SearchState.Idle
        _isErrorToastShown.value = false
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
