package ru.practicum.android.diploma.presentation.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.usecases.VacanciesInteractor
import ru.practicum.android.diploma.domain.models.filtermodels.FilterIndustry
import ru.practicum.android.diploma.domain.models.vacancy.Area
import ru.practicum.android.diploma.domain.models.vacancy.Salary
import ru.practicum.android.diploma.domain.models.vacancy.Vacancy
import ru.practicum.android.diploma.domain.models.vacancydetails.EmployerDetails
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val vacancies: List<Vacancy>) : SearchState()
    object Empty : SearchState()
    object NoInternet : SearchState()
    object ServerError : SearchState()
}

class SearchViewModel(
    private val vacanciesInteractor: VacanciesInteractor
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
                val result = vacanciesInteractor.searchVacancies(
                    query = query,
                    page = currentPage,
                    pageSize = 20,
                    filters = FilterIndustry("", "")
                )

                if (result.isSuccess) {
                    handleSuccess(result.getOrThrow())
                } else {
                    handleError(result.exceptionOrNull()?.message ?: "Ошибка сервера")
                }
            } finally {
                isLoading = false
            }
        }
    }

    private fun handleError(message: String) {
        _searchState.value = when {
            message.contains("сети", ignoreCase = true) -> SearchState.NoInternet
            message.contains("Сервер", ignoreCase = true) -> SearchState.ServerError
            else -> SearchState.ServerError
        }
    }

    fun resetSearch() {
        currentPage = 0
        maxPages = Int.MAX_VALUE
        _searchState.value = SearchState.Idle
    }

    private fun shouldSkipSearch(): Boolean = isLoading || currentPage >= maxPages

    private fun handleSuccess(data: List<VacancyDetails>) {
        val newItems = data.map { item ->
            Vacancy(
                id = item.id,
                name = item.name,
                salary = item.salary?.let { s ->
                    Salary(from = s.from, to = s.to, currency = s.currency, gross = null)
                },
                employer = EmployerDetails(
                    id = "",
                    name = item.employer,
                    logo = "",
                    description = null,
                    siteUrl = null
                ),
                area = Area(
                    id = "",
                    name = item.area,
                    country = null
                ),
                publishedAt = item.publishedAt ?: "",
                snippet = null
            )
        }

        currentPage += 1
        maxPages = Int.MAX_VALUE

        _searchState.value = if (newItems.isEmpty()) SearchState.Empty else SearchState.Success(newItems)
    }
}

