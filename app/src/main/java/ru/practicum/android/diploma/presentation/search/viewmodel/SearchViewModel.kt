package ru.practicum.android.diploma.presentation.search.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.data.dto.ResponseError
import ru.practicum.android.diploma.data.dto.ResponseSuccess
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.VacancySearchRequest
import ru.practicum.android.diploma.data.network.VacancySearchResponse
import ru.practicum.android.diploma.domain.models.vacancy.Salary
import ru.practicum.android.diploma.domain.models.vacancy.Vacancy

class SearchViewModel(private val client: NetworkClient) : ViewModel() {

    private val _vacancies = MutableLiveData<List<Vacancy>>(emptyList())
    val vacancies: LiveData<List<Vacancy>> get() = _vacancies

    private var currentPage = 0
    private var maxPages = 1
    private var isLoading = false
    private var searchText = ""

    fun searchVacancies(text: String) {
        Log.d("SearchVM", "searchVacancies called with text: $text")

        if (isLoading || currentPage >= maxPages) {
            Log.d("SearchVM", "Search skipped: isLoading=$isLoading, currentPage=$currentPage, maxPages=$maxPages")
            return
        }

        searchText = text
        isLoading = true

        viewModelScope.launch {
            val request = VacancySearchRequest(text = searchText, page = currentPage)
            Log.d("SearchVM", "Sending request: $request")

            try {
                val response = client.doRequest(request)
                Log.d("SearchVM", "Raw response: $response")

                when (response) {
                    is ResponseSuccess<*> -> {
                        val data = response.data as VacancySearchResponse
                        Log.d("SearchVM", "Parsed response: items=${data.items.size}, page=${data.page}, pages=${data.pages}")

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

                        Log.d("Results", "Mapped vacancies: ${newItems.map { it.name }}")// по этому логу  проверяем
                        // полученные от сервера вакансии

                        currentList.addAll(newItems)
                        _vacancies.value = currentList

                        currentPage = data.page + 1
                        maxPages = data.pages
                        Log.d("SearchVM", "Updated pagination: currentPage=$currentPage, maxPages=$maxPages")
                    }
                    is ResponseError -> {
                        Log.e("SearchVM", "Ошибка: ${response.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("SearchVM", "Exception during network request", e)
            } finally {
                isLoading = false
                Log.d("SearchVM", "isLoading set to false")
            }
        }
    }

    fun resetSearch() {
        currentPage = 0
        maxPages = 1
    }
}
