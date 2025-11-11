package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterSettingsBinding
import ru.practicum.android.diploma.domain.models.filtermodels.isAnyFilterApplied
import ru.practicum.android.diploma.presentation.filter.viewmodel.FilterViewModel
import ru.practicum.android.diploma.ui.model.FilterIndustryUI
import ru.practicum.android.diploma.ui.screens.filter.FilterClearManager
import ru.practicum.android.diploma.ui.screens.filter.FilterDataManager
import ru.practicum.android.diploma.ui.screens.filter.FilterNavigationManager
import ru.practicum.android.diploma.ui.screens.filter.FilterUIStateManager
import ru.practicum.android.diploma.ui.screens.filter.SalaryFieldManager

class FilterSettingsFragment : Fragment(R.layout.fragment_filter_settings) {

    private var _binding: FragmentFilterSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FilterViewModel by viewModel()
    private val filterPreferences: ru.practicum.android.diploma.data.storage.FilterPreferences by inject()

    private lateinit var uiStateManager: FilterUIStateManager
    private lateinit var dataManager: FilterDataManager
    private lateinit var salaryManager: SalaryFieldManager
    private lateinit var clearManager: FilterClearManager
    private lateinit var navigationManager: FilterNavigationManager

    private var isViewCreated = false

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
        initializeManagers()
        loadDraftFilters()
        setupFragmentResultListeners()
        setupClickListeners()
        setupTextWatchers()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isViewCreated = false
        _binding = null
    }

    private fun initializeManagers() {
        uiStateManager = FilterUIStateManager(binding, requireContext())
        dataManager = FilterDataManager(filterPreferences)
        salaryManager = SalaryFieldManager(
            binding.editSalary,
            binding.salaryField,
            binding.clearIcon,
            ::onFilterChanged
        )
        clearManager = FilterClearManager(uiStateManager, dataManager, salaryManager)
        navigationManager = FilterNavigationManager(findNavController(), clearManager)
    }

    private fun onFilterChanged() {
        saveDraftFilters()
        updateButtonsVisibility()
    }

    private fun observeViewModel() {
        viewModel.filtersState.observe(viewLifecycleOwner) { filters ->
            if (!dataManager.loadDraftFilters().isAnyFilterApplied()) {
                updateUIWithFilters(filters)
            }
        }
    }

    private fun loadDraftFilters() {
        val draftFilters = dataManager.loadDraftFilters()
        if (draftFilters.isAnyFilterApplied()) {
            updateUIWithDraftFilters(draftFilters)
        } else {
            loadSavedFilters()
        }
    }

    private fun loadSavedFilters() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadCurrentFilters()
        }
    }

    private fun updateUIWithDraftFilters(filters: ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters) {
        updateIndustryUI(filters.industry?.name)
        updateSalaryUI(filters.salary)
        updateCheckboxUI(filters.hideWithoutSalary)
        updateLocationUI(filters.region)
        updateButtonsVisibility()
    }

    private fun updateUIWithFilters(filters: ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters) {
        updateIndustryUI(filters.industry?.name)
        updateSalaryUI(filters.salary)
        updateCheckboxUI(filters.hideWithoutSalary)
        updateLocationUI(filters.region)
        updateButtonsVisibility()
    }

    private fun updateIndustryUI(industryName: String?) {
        industryName ?: return
        if (binding.editIndustry.text?.toString() != industryName) {
            binding.editIndustry.setText(industryName)
            dataManager.selectedIndustry = FilterIndustryUI(
                id = industryName.hashCode(),
                name = industryName
            )
            uiStateManager.updateIconAndState(binding.industry, industryName)
        }
    }

    private fun updateSalaryUI(salary: Int?) {
        salary?.let {
            val currentSalary = binding.editSalary.text?.toString()?.toIntOrNull()
            if (currentSalary != salary) {
                binding.editSalary.setText(salary.toString())
            }
        }
    }

    private fun updateCheckboxUI(hideWithoutSalary: Boolean?) {
        val newState = hideWithoutSalary ?: false
        if (binding.checkbox.isChecked != newState) {
            binding.checkbox.isChecked = newState
        }
    }

    private fun updateLocationUI(region: ru.practicum.android.diploma.domain.models.filtermodels.Region?) {
        region ?: return

        dataManager.selectedRegion = if (region.name.isNotEmpty()) region else null
        dataManager.selectedCountry = region.country

        val locationText = when {
            region.country != null && region.name.isNotBlank() ->
                "${region.country.name}, ${region.name}"
            region.country != null -> region.country.name
            else -> region.name
        }

        if (binding.editJobLocation.text?.toString() != locationText) {
            binding.editJobLocation.setText(locationText)
            uiStateManager.updateIconAndState(binding.jobLocation, locationText)
        }
    }

    private fun setupFragmentResultListeners() {
        setFragmentResultListener(INDUSTRY_RESULT_KEY) { _, bundle ->
            bundle.getParcelable<FilterIndustryUI>(SELECTED_INDUSTRY_KEY)?.let { industry ->
                dataManager.selectedIndustry = industry
                binding.editIndustry.setText(industry.name)
                uiStateManager.updateIconAndState(binding.industry, industry.name)
                onFilterChanged()
            }
        }

        setFragmentResultListener(WORKPLACE_RESULT_KEY) { _, bundle ->
            dataManager.selectedCountry = bundle.getParcelable("country")
            dataManager.selectedRegion = bundle.getParcelable("region")

            val text = when {
                dataManager.selectedRegion != null && dataManager.selectedCountry != null ->
                    "${dataManager.selectedCountry!!.name}, ${dataManager.selectedRegion!!.name}"
                dataManager.selectedCountry != null -> dataManager.selectedCountry!!.name
                else -> ""
            }

            binding.editJobLocation.setText(text)
            uiStateManager.updateIconAndState(binding.jobLocation, text)
            onFilterChanged()
        }
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

    private fun setupTextWatchers() {
        setupJobLocationField()
        setupIndustryField()
        setupCheckbox()
        salaryManager.setupSalaryField()
    }

    private fun setupJobLocationField() {
        binding.jobLocation.endIconMode = com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM
        binding.editJobLocation.addTextChangedListener {
            uiStateManager.updateIconAndState(binding.jobLocation, it?.toString().orEmpty())
            onFilterChanged()
        }
        binding.jobLocation.setEndIconOnClickListener {
            navigationManager.navigateOrClearField(
                binding.editJobLocation,
                binding.jobLocation,
                FilterNavigationManager.FieldType.JOB_LOCATION
            )
            onFilterChanged()
        }
    }

    private fun setupIndustryField() {
        binding.industry.endIconMode = com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM
        binding.editIndustry.addTextChangedListener {
            uiStateManager.updateIconAndState(binding.industry, it?.toString().orEmpty())
            onFilterChanged()
        }
        binding.industry.setEndIconOnClickListener {
            navigationManager.navigateOrClearField(
                binding.editIndustry,
                binding.industry,
                FilterNavigationManager.FieldType.INDUSTRY
            )
            onFilterChanged()
        }
    }

    private fun setupCheckbox() {
        binding.checkbox.setOnCheckedChangeListener { _, _ ->
            onFilterChanged()
        }
    }

    private fun updateButtonsVisibility() {
        uiStateManager.updateButtonsVisibility(uiStateManager.hasAnyFilterApplied())
    }

    private fun applyFiltersAndReturn() {
        dataManager.applyDraftFilters()
        setFragmentResult(
            FILTER_RESULT_KEY,
            Bundle().apply {
                putBoolean(FILTERS_APPLIED_KEY, true)
            }
        )
        findNavController().navigateUp()
    }

    private fun saveDraftFilters() {
        if (!isViewCreated) return
        dataManager.saveDraftFilters(salaryManager.getSalaryText(), binding.checkbox.isChecked)
    }

    private fun clearAllFields() {
        clearManager.clearAllFields(
            binding.editJobLocation,
            binding.jobLocation,
            binding.editIndustry,
            binding.industry,
            binding.checkbox
        )
        updateButtonsVisibility()

        viewModel.clearFilters()

        setFragmentResult(
            FILTER_RESULT_KEY,
            Bundle().apply {
                putBoolean(FILTERS_APPLIED_KEY, true)
            }
        )

        Toast.makeText(requireContext(), "Фильтры сброшены", Toast.LENGTH_SHORT).show()
    }
}
