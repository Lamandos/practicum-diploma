package ru.practicum.android.diploma.presentation.search.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.data.dto.Response
import ru.practicum.android.diploma.data.dto.ResponseError
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.VacancySearchRequest
import ru.practicum.android.diploma.data.network.VacancySearchResponse
import ru.practicum.android.diploma.domain.models.vacancy.Salary
import ru.practicum.android.diploma.domain.models.vacancy.Vacancy
import java.io.IOException

class SearchViewModel(
    private val client: NetworkClient
) : ViewModel() {

    companion object {
        private const val TAG_SEARCH_VM = "SearchVM"
        private const val TAG_RESULTS = "Results"
    }

    private var searchText = ""
    private var isLoading = false
    private var currentPage = 0
    private var maxPages = Int.MAX_VALUE

    private val _vacancies = MutableLiveData<List<Vacancy>>()
    val vacancies: LiveData<List<Vacancy>> get() = _vacancies

    fun searchVacancies(text: String) {
        if (shouldSkipSearch()) return

        searchText = text
        isLoading = true
        Log.d(TAG_SEARCH_VM, "API Token before request: ${BuildConfig.API_ACCESS_TOKEN}")

        viewModelScope.launch {
            val request = VacancySearchRequest(text = searchText, page = currentPage)
            try {
                val response = client.doRequest(request)
                handleResponse(response)
            } catch (e: IOException) {
                Log.e(TAG_SEARCH_VM, "Network error", e)
            } catch (e: HttpException) {
                Log.e(TAG_SEARCH_VM, "Server error", e)
            } finally {
                isLoading = false
                Log.d(TAG_SEARCH_VM, "isLoading set to false")
            }
        }
    }

    fun resetSearch() {
        searchText = ""
        currentPage = 0
        maxPages = Int.MAX_VALUE
        _vacancies.value = emptyList()
    }

    private fun shouldSkipSearch(): Boolean {
        val skip = isLoading || currentPage >= maxPages
        return skip
    }

    private fun handleResponse(response: Response) {
        when (response) {
            is ResponseSuccess<*> -> handleSuccess(response.data as VacancySearchResponse)
            is ResponseError -> Log.e(TAG_SEARCH_VM, "Ошибка: ${response.message}")
        }
    }

    private fun handleSuccess(data: VacancySearchResponse) {
        val currentList = _vacancies.value.orEmpty().toMutableList()
        val newItems = data.items.map { item ->
            Vacancy(
                id = item.id,
                name = item.name,
                salary = item.salary?.let { s ->
                    Salary(
                        from = s.from,
                        to = s.to,
                        currency = s.currency,
                        gross = s.gross
                    )
                },
                employer = item.employer,
                area = item.area,
                publishedAt = "",
                snippet = null
            )
        }

        val vacanciesLog = newItems.joinToString(separator = " | ") { vacancy ->
            val salaryStr = vacancy.salary?.let { "${it.from}-${it.to} ${it.currency}" } ?: "Не указано"
            val city = vacancy.area.name
            val employer = vacancy.employer.name
            "${vacancy.name} ($city, $salaryStr, $employer)"
        }

        Log.d(TAG_RESULTS, "Вакансии страницы ${data.page}: $vacanciesLog")

        currentList.addAll(newItems)
        _vacancies.value = currentList
        currentPage = data.page + 1
        maxPages = data.pages
    }
}
