package ru.practicum.android.diploma.presentation.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.usecases.FilterInteractor
import ru.practicum.android.diploma.domain.interactors.SearchVacanciesInteractor
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters
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
    private val interactor: SearchVacanciesInteractor,
    private val filterInteractor: FilterInteractor
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>(SearchState.Idle)
    val searchState: LiveData<SearchState> get() = _searchState

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _isErrorToastShown = MutableLiveData(false)
    val isErrorToastShown: LiveData<Boolean> get() = _isErrorToastShown

    private val _showLoadingState = MutableLiveData<Boolean>()
    val showLoadingState: LiveData<Boolean> get() = _showLoadingState

    // LiveData для состояния фильтров
    private val _isFilterApplied = MutableLiveData<Boolean>()
    val isFilterApplied: LiveData<Boolean> get() = _isFilterApplied

    private var isLoading = false
    private var isLoadingNextPageInternal = false
    private var currentPage = 0
    private var maxPages = Int.MAX_VALUE
    private var lastQuery: String = ""

    fun checkFiltersApplied() {
        viewModelScope.launch {
            val filters = filterInteractor.getFilters()
            _isFilterApplied.value = isAnyFilterApplied(filters)
        }
    }

    val totalFoundCount: Int
        get() = interactor.totalFoundCount

    val isLoadingNextPage: Boolean
        get() = isLoadingNextPageInternal

    init {
        loadCurrentFilters()
    }

    private fun loadCurrentFilters() {
        viewModelScope.launch {
            val filters = filterInteractor.getFilters()
            _isFilterApplied.value = isAnyFilterApplied(filters)
        }
    }

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
                // Получаем текущие фильтры
                val filters = filterInteractor.getFilters()

                // ДЕТАЛЬНОЕ ЛОГИРОВАНИЕ ФИЛЬТРОВ
                println("=== SEARCH FILTERS DEBUG ===")
                println("Search query: $query")
                println("Filters object: $filters")
                println("Salary from filters: ${filters.salary}")
                println("Hide without salary: ${filters.hideWithoutSalary}")
                println("Are filters applied: ${isAnyFilterApplied(filters)}")
                println("============================")

                val result = interactor.searchVacancies(
                    query = query,
                    page = currentPage + 1,
                    pageSize = PAGE_SIZE,
                    filters = if (isAnyFilterApplied(filters)) filters else null
                )

                if (result.isSuccess) {
                    val vacancies = result.getOrThrow()
                    println("DEBUG: Search successful, found ${vacancies.size} vacancies")
                    handleSuccess(vacancies, append = false)
                } else {
                    println("DEBUG: Search failed: ${result.exceptionOrNull()}")
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
                // Получаем текущие фильтры для пагинации
                val filters = filterInteractor.getFilters()
                val result = interactor.searchVacancies(
                    query = lastQuery,
                    page = currentPage + 1,
                    pageSize = PAGE_SIZE,
                    filters = if (isAnyFilterApplied(filters)) filters else null
                )

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
        viewModelScope.launch {
            val filters = filterInteractor.getFilters()
            val targetSalary = filters.salary

            val filteredVacancies = if (targetSalary != null) {
                // ФИЛЬТРАЦИЯ: зарплата ≥ targetSalary ИЛИ targetSalary в диапазоне
                filterVacanciesBySalary(vacancies, targetSalary)
            } else {
                vacancies
            }

            println("DEBUG: Salary filtering - from ${vacancies.size} to ${filteredVacancies.size} vacancies")
            logSalaryInfo(filteredVacancies, targetSalary)

            val newItems = filteredVacancies.map { it.toVacancy() }
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
    }

    private fun filterVacanciesBySalary(
        vacancies: List<VacancyDetails>,
        targetSalary: Int
    ): List<VacancyDetails> {
        return vacancies.filter { vacancy ->
            val salary = vacancy.salary
            when {
                salary == null -> false // Исключаем вакансии без зарплаты
                // Случай 1: "от X" - зарплата ≥ targetSalary
                salary.from != null && salary.from >= targetSalary -> true
                // Случай 2: "до Y" - targetSalary ≤ Y
                salary.to != null && targetSalary <= salary.to -> true
                // Случай 3: "от X до Y" - targetSalary в диапазоне X..Y
                salary.from != null && salary.to != null ->
                    targetSalary in salary.from..salary.to
                else -> false
            }
        }
    }

    private fun logSalaryInfo(vacancies: List<VacancyDetails>, targetSalary: Int?) {
        println("=== SALARY FILTERING INFO ===")
        println("Target salary: $targetSalary")
        println("Found ${vacancies.size} vacancies after filtering")
        vacancies.forEachIndexed { index, vacancy ->
            val salary = vacancy.salary
            val matches = when {
                salary == null -> "NO SALARY"
                salary.from != null && salary.from >= targetSalary!! -> "MATCH (≥)"
                salary.to != null && targetSalary!! <= salary.to -> "MATCH (≤)"
                salary.from != null && salary.to != null && targetSalary!! in salary.from..salary.to -> "MATCH (range)"
                else -> "NO MATCH"
            }
            val salaryText = when {
                salary?.from != null && salary.to != null -> "${salary.from}-${salary.to}"
                salary?.from != null -> "от ${salary.from}"
                salary?.to != null -> "до ${salary.to}"
                else -> "не указана"
            }
            println("$index: $salaryText - $matches")
        }
        println("=============================")
    }
    // SearchViewModel.kt - добавить метод для дополнительной фильтрации
    private fun filterVacanciesBySalaryRange(
        vacancies: List<VacancyDetails>,
        targetSalary: Int
    ): List<VacancyDetails> {
        return vacancies.filter { vacancy ->
            val salary = vacancy.salary
            when {
                salary == null -> false // Исключаем вакансии без зарплаты
                salary.from != null && salary.to != null ->
                    targetSalary in salary.from..salary.to
                salary.from != null -> targetSalary >= salary.from
                salary.to != null -> targetSalary <= salary.to
                else -> false
            }
        }
    }


    // Метод для принудительного обновления поиска с текущими фильтрами
    fun refreshSearchWithCurrentFilters() {
        if (lastQuery.isNotBlank()) {
            searchVacancies(lastQuery)
        }
    }

    private fun isAnyFilterApplied(filters: VacancyFilters): Boolean {
        return filters.region != null ||
            filters.industry != null ||
            filters.salary != null ||
            filters.hideWithoutSalary == true
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
