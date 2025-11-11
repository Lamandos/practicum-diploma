package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.storage.FilterPreferences
import ru.practicum.android.diploma.databinding.FragmentFilterSettingsBinding
import ru.practicum.android.diploma.domain.models.filtermodels.Industry
import ru.practicum.android.diploma.domain.models.filtermodels.Region
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters
import ru.practicum.android.diploma.domain.models.vacancy.Country
import ru.practicum.android.diploma.presentation.filter.viewmodel.FilterViewModel
import ru.practicum.android.diploma.ui.model.FilterIndustryUI
import ru.practicum.android.diploma.ui.screens.filter.FilterFieldHandler
import ru.practicum.android.diploma.ui.screens.filter.FilterStateManager
import ru.practicum.android.diploma.ui.screens.filter.FilterUIManager

class FilterSettingsFragment : Fragment(R.layout.fragment_filter_settings) {

    private var _binding: FragmentFilterSettingsBinding? = null
    val binding get() = _binding!!

    private val viewModel: FilterViewModel by viewModel()
    private val filterPreferences: FilterPreferences by inject()

    var selectedIndustry: FilterIndustryUI? = null
    var selectedCountry: Country? = null
    var selectedRegion: Region? = null

    var isViewCreated = false
    private var stateManager: FilterStateManager? = null
    private var uiManager: FilterUIManager? = null
    private var fieldHandler: FilterFieldHandler? = null

    companion object {
        private const val FILTER_RESULT_KEY = "filter_result"
        private const val INDUSTRY_RESULT_KEY = "industry_result"
        private const val WORKPLACE_RESULT_KEY = "workplace_result"
        private const val FILTERS_APPLIED_KEY = "filters_applied"
        private const val SELECTED_INDUSTRY_KEY = "selected_industry"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFilterSettingsBinding.bind(view)
        isViewCreated = true

        initManagers()
        setupFragmentResultListeners()
        setupClickListeners()
        fieldHandler?.setupTextWatchers()
        observeViewModel()

        stateManager?.loadDraftFilters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isViewCreated = false
        _binding = null
        stateManager = null
        uiManager = null
        fieldHandler = null
    }

    private fun initManagers() {
        stateManager = FilterStateManager(this, viewModel, filterPreferences)
        uiManager = FilterUIManager(this)
        fieldHandler = FilterFieldHandler(this)
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAccept.setOnClickListener {
            applyFiltersAndReturn()
        }

        binding.btnDeny.setOnClickListener {
            clearAllFields()
        }
    }

    private fun setupFragmentResultListeners() {
        setFragmentResultListener(INDUSTRY_RESULT_KEY) { _, bundle ->
            bundle.getParcelable<FilterIndustryUI>(SELECTED_INDUSTRY_KEY)?.let { industry ->
                selectedIndustry = industry
                binding.editIndustry.setText(industry.name)
                uiManager?.updateIconAndState(binding.industry, industry.name)
                uiManager?.updateButtonsVisibility()
                saveDraftFilters()
            }
        }

        setFragmentResultListener(WORKPLACE_RESULT_KEY) { _, bundle ->
            selectedCountry = bundle.getParcelable("country")
            selectedRegion = bundle.getParcelable("region")

            val text = buildLocationText(selectedCountry, selectedRegion)
            binding.editJobLocation.setText(text)
            uiManager?.updateIconAndState(binding.jobLocation, text)
            uiManager?.updateButtonsVisibility()
            saveDraftFilters()
        }

        updateButtonsVisibility()
    }

    private fun observeViewModel() {
        viewModel.filtersState.observe(viewLifecycleOwner) { filters ->
            if (stateManager?.hasUnsavedChanges != true) {
                updateUIWithFilters(filters)
            }
        }
    }

    fun updateButtonsVisibility() = uiManager?.updateButtonsVisibility()

    fun updateIconAndState(layout: TextInputLayout, text: String) =
        uiManager?.updateIconAndState(layout, text)

    fun updateFilterAppliedState() {
        val hasFilters = hasAnyFilterApplied()
        stateManager?.isFilterApplied = hasFilters
    }

    fun saveDraftFilters() = stateManager?.saveDraftFilters()

    fun hasAnyFilterApplied(): Boolean = stateManager?.hasAnyFilterApplied() ?: false

    fun loadSavedFilters() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadCurrentFilters()
        }
    }

    fun clearAllFields() {
        uiManager?.clearAllFields()
        stateManager?.clearAllFilters()
        setFragmentResult(FILTER_RESULT_KEY, Bundle().apply { putBoolean(FILTERS_APPLIED_KEY, true) })
        android.widget.Toast.makeText(requireContext(), "Фильтры сброшены", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun applyFiltersAndReturn() {
        stateManager?.applyFiltersAndReturn()
        setFragmentResult(FILTER_RESULT_KEY, Bundle().apply { putBoolean(FILTERS_APPLIED_KEY, true) })
        findNavController().navigateUp()
    }

    private fun updateUIWithFilters(filters: VacancyFilters) {
        updateIndustryUI(filters.industry)
        updateCheckboxUI(filters.hideWithoutSalary)

        filters.salary?.let { salary ->
            val currentSalary = binding.editSalary.text?.toString()?.toIntOrNull()
            if (currentSalary != salary) {
                binding.editSalary.setText(salary.toString())
            }
        }

        filters.region?.let { region ->
            selectedRegion = if (region.name.isNotEmpty()) region else null
            selectedCountry = region.country

            val locationText = buildLocationText(selectedCountry, selectedRegion)
            if (binding.editJobLocation.text?.toString() != locationText) {
                binding.editJobLocation.setText(locationText)
                uiManager?.updateIconAndState(binding.jobLocation, locationText)
            }
        }

        uiManager?.updateButtonsVisibility()
    }

    private fun updateIndustryUI(industry: Industry?) {
        industry ?: return
        if (binding.editIndustry.text?.toString() != industry.name) {
            binding.editIndustry.setText(industry.name)
            selectedIndustry = FilterIndustryUI(id = industry.id.toIntOrNull() ?: 0, name = industry.name)
            uiManager?.updateIconAndState(binding.industry, industry.name)
        }
    }

    private fun updateCheckboxUI(hideWithoutSalary: Boolean?) {
        val newState = hideWithoutSalary ?: false
        if (binding.checkbox.isChecked != newState) {
            binding.checkbox.isChecked = newState
        }
    }

    private fun buildLocationText(country: Country?, region: Region?): String {
        return when {
            country != null && region != null && region.name.isNotBlank() ->
                "${country.name}, ${region.name}"
            country != null -> country.name
            region != null -> region.name
            else -> ""
        }
    }
}
