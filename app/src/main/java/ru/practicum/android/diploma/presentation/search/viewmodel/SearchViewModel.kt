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
import ru.practicum.android.diploma.domain.models.vacancy.Salary
import ru.practicum.android.diploma.domain.models.vacancy.Vacancy

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val vacancies: List<Vacancy>) : SearchState()
    object Empty : SearchState()
    object NoInternet : SearchState()
    object ServerError : SearchState()
}

class SearchViewModel(
    private val client: NetworkClient
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>(SearchState.Idle)
    val searchState: LiveData<SearchState> get() = _searchState

    private var isLoading = false
    private var currentPage = 0
    private var maxPages = Int.MAX_VALUE

    fun setLoading() {
        _searchState.value = SearchState.Loading
    }

    fun searchVacancies(query: String) {
        if (shouldSkipSearch()) return

        isLoading = true
        viewModelScope.launch {
            try {
                val response = client.doRequest(VacancySearchRequest(text = query, page = currentPage))
                when (response) {
                    is ResponseSuccess<*> -> handleSuccess(response.data as VacancySearchResponse)
                    is ResponseError -> handleError(response)
                }
            } finally {
                isLoading = false
            }
        }
    }

    private fun handleError(response: ResponseError) {
        _searchState.value = when {
            response.message.contains("сети", ignoreCase = true) -> SearchState.NoInternet
            response.message.contains("Сервер", ignoreCase = true) -> SearchState.ServerError
            else -> SearchState.ServerError
        }
    }

    fun resetSearch() {
        currentPage = 0
        maxPages = Int.MAX_VALUE
        _searchState.value = SearchState.Idle
    }

    private fun shouldSkipSearch(): Boolean = isLoading || currentPage >= maxPages

    private fun handleResponse(response: Response) {
        when (response) {
            is ResponseSuccess<*> -> handleSuccess(response.data as VacancySearchResponse)
            is ResponseError -> _searchState.postValue(SearchState.ServerError)
        }
    }

    private fun handleSuccess(data: VacancySearchResponse) {
        val newItems = data.items.map { item ->
            Vacancy(
                id = item.id,
                name = item.name,
                salary = item.salary?.let { s ->
                    Salary(from = s.from, to = s.to, currency = s.currency, gross = s.gross)
                },
                employer = item.employer,
                area = item.area,
                publishedAt = "",
                snippet = null
            )
        }

        currentPage = data.page + 1
        maxPages = data.pages

        _searchState.value = if (newItems.isEmpty()) SearchState.Empty else SearchState.Success(newItems)
    }
}

