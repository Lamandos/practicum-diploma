package ru.practicum.android.diploma.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters

class FilterPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("filter_preferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_FILTERS = "vacancy_filters"
    }

    fun saveFilters(filters: VacancyFilters) {
        val filtersJson = gson.toJson(filters)
        sharedPreferences.edit().putString(KEY_FILTERS, filtersJson).apply()
    }

    fun getFilters(): VacancyFilters {
        val filtersJson = sharedPreferences.getString(KEY_FILTERS, null)
        return if (filtersJson != null) {
            gson.fromJson(filtersJson, VacancyFilters::class.java) ?: VacancyFilters()
        } else {
            VacancyFilters()
        }
    }

    fun clearFilters() {
        sharedPreferences.edit().remove(KEY_FILTERS).apply()
    }
}
