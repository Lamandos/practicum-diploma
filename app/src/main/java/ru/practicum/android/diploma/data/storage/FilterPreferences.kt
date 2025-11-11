package ru.practicum.android.diploma.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters

class FilterPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("filter_preferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_FILTERS = "vacancy_filters"
        private const val KEY_DRAFT_FILTERS = "draft_filters" // Новый ключ для черновиков
    }

    // Основные методы для рабочих фильтров
    fun saveFilters(filters: VacancyFilters) {
        val filtersJson = gson.toJson(filters)
        sharedPreferences.edit().putString(KEY_FILTERS, filtersJson).apply()
    }

    fun getFilters(): VacancyFilters {
        val filtersJson = sharedPreferences.getString(KEY_FILTERS, null)
        return if (filtersJson != null) {
            val type = object : TypeToken<VacancyFilters>() {}.type
            gson.fromJson<VacancyFilters>(filtersJson, type) ?: VacancyFilters()
        } else {
            VacancyFilters()
        }
    }

    fun clearFilters() {
        sharedPreferences.edit().remove(KEY_FILTERS).apply()
    }

    // Новые методы для черновиков
    fun saveDraftFilters(filters: VacancyFilters) {
        val filtersJson = gson.toJson(filters)
        sharedPreferences.edit().putString(KEY_DRAFT_FILTERS, filtersJson).apply()
    }

    fun getDraftFilters(): VacancyFilters {
        val filtersJson = sharedPreferences.getString(KEY_DRAFT_FILTERS, null)
        return if (filtersJson != null) {
            val type = object : TypeToken<VacancyFilters>() {}.type
            gson.fromJson<VacancyFilters>(filtersJson, type) ?: VacancyFilters()
        } else {
            VacancyFilters()
        }
    }

    fun clearDraftFilters() {
        sharedPreferences.edit().remove(KEY_DRAFT_FILTERS).apply()
    }

    // Метод для применения черновиков (копирует черновики в основные фильтры)
    fun applyDraftFilters() {
        val draftFilters = getDraftFilters()
        saveFilters(draftFilters)
        clearDraftFilters()
    }
}
