package ru.practicum.android.diploma.presentation.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.data.dto.Response
import ru.practicum.android.diploma.data.dto.ResponseError
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.VacancySearchRequest
import ru.practicum.android.diploma.data.network.VacancySearchResponse
import ru.practicum.android.diploma.data.network.toDomain
import ru.practicum.android.diploma.domain.models.vacancy.Vacancy

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val vacancies: List<Vacancy>) : SearchState()
    object Empty : SearchState()
    object Error : SearchState()
}

class SearchViewModel(
    private val client: NetworkClient
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>(SearchState.Idle)
    val searchState: LiveData<SearchState> get() = _searchState

    private var isLoading = false
    private var isLoadingNextPageInternal = false
    private var currentPage = 0
    private var maxPages = Int.MAX_VALUE
    private var lastQuery: String = ""
    private var _totalFoundCount: Int = 0
    val totalFoundCount: Int get() = _totalFoundCount
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

        isLoading = true
        _searchState.value = SearchState.Loading
        viewModelScope.launch {
            try {
                val response = client.doRequest(VacancySearchRequest(text = query, page = currentPage))
                handleResponse(response, append = false)
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
                val response = client.doRequest(
                    VacancySearchRequest(text = lastQuery, page = currentPage)
                )
                handleResponse(response, append = true)
            } finally {
                isLoadingNextPageInternal = false
                _showLoadingState.postValue(false)
            }
        }
    }

    fun resetSearch() {
        currentPage = 0
        maxPages = Int.MAX_VALUE
        lastQuery = ""
        _totalFoundCount = 0
        isLoadingNextPageInternal = false
        _searchState.value = SearchState.Idle
    }

    private fun handleResponse(response: Response, append: Boolean) {
        when (response) {
            is ResponseSuccess<*> -> handleSuccess(response.data as VacancySearchResponse, append)
            is ResponseError -> {
                if (!append) {
                    _searchState.postValue(SearchState.Empty)
                }
                isLoadingNextPageInternal = false
            }
        }
    }

    private fun handleSuccess(data: VacancySearchResponse, append: Boolean) {
        val newItems = data.items.map { it.toDomain() }

        if (!append) {
            _totalFoundCount = data.found
        }

        currentPage = data.page + 1
        maxPages = data.pages

        if (append) {
            val current = (_searchState.value as? SearchState.Success)?.vacancies ?: emptyList()
            _searchState.postValue(SearchState.Success(current + newItems))
        } else {
            _searchState.postValue(
                if (newItems.isEmpty()) SearchState.Empty else SearchState.Success(newItems)
            )
        }

        isLoadingNextPageInternal = false
        _showLoadingState.postValue(false)
    }
}
