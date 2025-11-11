package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterSettingsBinding
import ru.practicum.android.diploma.domain.models.filtermodels.Industry
import ru.practicum.android.diploma.domain.models.filtermodels.Region
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters
import ru.practicum.android.diploma.domain.models.filtermodels.isAnyFilterApplied
import ru.practicum.android.diploma.domain.models.vacancy.Country
import ru.practicum.android.diploma.presentation.filter.viewmodel.FilterViewModel
import ru.practicum.android.diploma.ui.model.FilterIndustryUI

class FilterSettingsFragment : Fragment(R.layout.fragment_filter_settings) {

    private var _binding: FragmentFilterSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FilterViewModel by viewModel()
    private val filterPreferences: ru.practicum.android.diploma.data.storage.FilterPreferences by inject()

    private var selectedIndustry: FilterIndustryUI? = null
    private var selectedCountry: Country? = null
    private var selectedRegion: Region? = null

    private var isViewCreated = false
    private var hasUnsavedChanges = false
    private var isFilterApplied = false

    companion object {
        private const val FILTER_RESULT_KEY = "filter_result"
        private const val INDUSTRY_RESULT_KEY = "industry_result"
        private const val WORKPLACE_RESULT_KEY = "workplace_result"
        private const val FILTERS_APPLIED_KEY = "filters_applied"
        private const val SELECTED_INDUSTRY_KEY = "selected_industry"
        private const val JOB_LOCATION_FIELD = "jobLocation"
        private const val INDUSTRY_FIELD = "industry"
        private const val CURRENCY_RUB = "RUB"
        private const val FILTER_DRAFT_TAG = "FilterDraft" // Константа для тега логов
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFilterSettingsBinding.bind(view)
        isViewCreated = true

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

    private fun updateUIWithDraftFilters(filters: VacancyFilters) {
        // Обновляем отрасль из черновиков
        filters.industry?.let { industry ->
            binding.editIndustry.setText(industry.name)
            selectedIndustry = FilterIndustryUI(
                id = industry.id.toIntOrNull() ?: 0,
                name = industry.name
            )
            updateIconAndState(binding.industry, industry.name)
        }

        // Обновляем зарплату из черновиков
        filters.salary?.let { salary ->
            binding.editSalary.setText(salary.toString())
        }

        // Обновляем чекбокс из черновиков
        binding.checkbox.isChecked = filters.hideWithoutSalary ?: false

        // Обновляем регион из черновиков
        filters.region?.let { region ->
            selectedRegion = if (region.name.isNotEmpty()) region else null
            selectedCountry = region.country

            val locationText = when {
                region.country != null && region.name.isNotBlank() ->
                    "${region.country.name}, ${region.name}"
                region.country != null -> region.country.name
                else -> region.name
            }

            binding.editJobLocation.setText(locationText)
            updateIconAndState(binding.jobLocation, locationText)
        }

        updateButtonsVisibility()
    }

    private fun observeViewModel() {
        viewModel.filtersState.observe(viewLifecycleOwner) { filters ->
            // Используем только для начальной загрузки, если нет черновиков
            if (!hasUnsavedChanges) {
                updateUIWithFilters(filters)
            }
        }
    }

    private fun loadSavedFilters() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadCurrentFilters()
        }
    }

    private fun updateUIWithFilters(filters: VacancyFilters) {
        // Обновляем отрасль
        filters.industry?.let { industry ->
            if (binding.editIndustry.text?.toString() != industry.name) {
                binding.editIndustry.setText(industry.name)
                selectedIndustry = FilterIndustryUI(
                    id = industry.id.toIntOrNull() ?: 0,
                    name = industry.name
                )
                updateIconAndState(binding.industry, industry.name)
            }
        }

        // Обновляем зарплату
        filters.salary?.let { salary ->
            val currentSalary = binding.editSalary.text?.toString()?.toIntOrNull()
            if (currentSalary != salary) {
                binding.editSalary.setText(salary.toString())
            }
        }

        // Обновляем чекбокс "Только с зарплатой"
        val currentCheckboxState = binding.checkbox.isChecked
        val newCheckboxState = filters.hideWithoutSalary ?: false
        if (currentCheckboxState != newCheckboxState) {
            binding.checkbox.isChecked = newCheckboxState
        }

        // Обновляем регион
        filters.region?.let { region ->
            selectedRegion = if (region.name.isNotEmpty()) region else null
            selectedCountry = region.country

            val locationText = when {
                region.country != null && region.name.isNotBlank() ->
                    "${region.country.name}, ${region.name}"
                region.country != null -> region.country.name
                else -> region.name
            }

            if (binding.editJobLocation.text?.toString() != locationText) {
                binding.editJobLocation.setText(locationText)
                updateIconAndState(binding.jobLocation, locationText)
            }
        }

        updateButtonsVisibility()
    }

    private fun setupFragmentResultListeners() {
        setFragmentResultListener(INDUSTRY_RESULT_KEY) { _, bundle ->
            bundle.getParcelable<FilterIndustryUI>(SELECTED_INDUSTRY_KEY)?.let { industry ->
                selectedIndustry = industry
                binding.editIndustry.setText(industry.name)
                updateIconAndState(binding.industry, industry.name)
                updateButtonsVisibility()
                saveDraftFilters()
            }
        }

        setFragmentResultListener(WORKPLACE_RESULT_KEY) { _, bundle ->
            selectedCountry = bundle.getParcelable("country")
            selectedRegion = bundle.getParcelable("region")

            val text = when {
                selectedRegion != null && selectedCountry != null ->
                    "${selectedCountry!!.name}, ${selectedRegion!!.name}"
                selectedCountry != null -> selectedCountry!!.name
                else -> ""
            }

            binding.editJobLocation.setText(text)
            updateIconAndState(binding.jobLocation, text)
            updateButtonsVisibility()
            saveDraftFilters()
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

        binding.clearIcon.setOnClickListener {
            binding.editSalary.text?.clear()
            saveDraftFilters()
        }
    }

    private fun setupTextWatchers() {
        val jobLocationLayout: TextInputLayout = binding.jobLocation
        val jobLocationEditText: TextInputEditText = binding.editJobLocation
        val industryLayout: TextInputLayout = binding.industry
        val industryEditText: TextInputEditText = binding.editIndustry

        jobLocationLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
        industryLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM

        jobLocationEditText.addTextChangedListener {
            updateIconAndState(jobLocationLayout, it?.toString().orEmpty())
            updateButtonsVisibility()
            saveDraftFilters()
        }

        industryEditText.addTextChangedListener {
            updateIconAndState(industryLayout, it?.toString().orEmpty())
            updateButtonsVisibility()
            saveDraftFilters()
        }

        jobLocationLayout.setEndIconOnClickListener {
            navigateOrClear(jobLocationEditText, jobLocationLayout, JOB_LOCATION_FIELD)
        }

        industryLayout.setEndIconOnClickListener {
            navigateOrClear(industryEditText, industryLayout, INDUSTRY_FIELD)
        }

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            updateButtonsVisibility()
            saveDraftFilters()
            updateFilterAppliedState()
        }

        updateIconAndState(jobLocationLayout, jobLocationEditText.text.toString())
        updateIconAndState(industryLayout, industryEditText.text.toString())

        setupSalaryField(binding.editSalary, binding.salaryField)
    }

    private fun updateFilterAppliedState() {
        val hasFilters = hasAnyFilterApplied()
        isFilterApplied = hasFilters
    }

    private fun setupSalaryField(editText: TextInputEditText, layout: TextInputLayout) {
        setupSalaryInputFilter(editText)
        setupSalaryTextWatcher(editText)
        setupSalaryFocusBehavior(editText)
        setupSalaryEditorDoneAction(editText, layout)
        setupSalaryClearIcon(editText)
    }

    private fun setupSalaryInputFilter(editText: TextInputEditText) {
        editText.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("[0-9]+"))) source else ""
        })
    }

    private fun setupSalaryTextWatcher(editText: TextInputEditText) {
        editText.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                    if (s.toString() == "0") {
                        binding.clearIcon.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Некорректное значение",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    updateButtonsVisibility()
                    saveDraftFilters()
                }
            }
        )
    }

    private fun setupSalaryFocusBehavior(editText: TextInputEditText) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !editText.text.isNullOrEmpty()) {
                binding.clearIcon.visibility = View.VISIBLE
            }
        }
    }

    private fun setupSalaryEditorDoneAction(
        editText: TextInputEditText,
        layout: TextInputLayout
    ) {
        editText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.clearIcon.visibility = View.GONE
                closeKeyboard(editText)

                editText.clearFocus()
                layout.clearFocus()

                binding.root.isFocusable = true
                binding.root.isFocusableInTouchMode = true
                binding.root.requestFocus()
                true
            } else {
                false
            }
        }
    }

    private fun setupSalaryClearIcon(editText: TextInputEditText) {
        binding.clearIcon.setOnClickListener {
            editText.text?.clear()
            saveDraftFilters()
        }
    }

    private fun updateButtonsVisibility() {
        val hasFilters = hasAnyFilterApplied()
        binding.btnAccept.visibility = if (hasFilters) View.VISIBLE else View.GONE
        binding.btnDeny.visibility = if (hasFilters) View.VISIBLE else View.GONE
        updateFilterAppliedState()
    }

    private fun hasAnyFilterApplied(): Boolean {
        return binding.editJobLocation.text?.isNotBlank() == true ||
            binding.editIndustry.text?.isNotBlank() == true ||
            binding.editSalary.text?.isNotBlank() == true ||
            binding.checkbox.isChecked
    }

    private fun applyFiltersAndReturn() {
        filterPreferences.applyDraftFilters()
        isFilterApplied = true

        setFragmentResult(
            FILTER_RESULT_KEY,
            Bundle().apply {
                putBoolean(FILTERS_APPLIED_KEY, true)
            }
        )
        findNavController().navigateUp()
    }

    private fun createFiltersFromUI(): VacancyFilters {
        val industry = selectedIndustry?.let { industryUI ->
            Industry(
                id = industryUI.id.toString(),
                name = industryUI.name,
                parentId = null
            )
        }

        val salary = binding.editSalary.text?.toString()?.toIntOrNull()
        val hideWithoutSalary = binding.checkbox.isChecked

        val regionToSave = when {
            selectedRegion != null -> selectedRegion
            selectedCountry != null -> Region(
                id = selectedCountry!!.id,
                name = "",
                country = selectedCountry
            )
            else -> null
        }

        return VacancyFilters(
            region = regionToSave,
            industry = industry,
            salary = salary,
            hideWithoutSalary = hideWithoutSalary,
            currency = CURRENCY_RUB
        )
    }

    private fun saveDraftFilters() {
        if (!isViewCreated) return

        hasUnsavedChanges = true
        val draftFilters = createFiltersFromUI()

        android.util.Log.d(FILTER_DRAFT_TAG, "Saving draft filters: $draftFilters")
        android.util.Log.d(FILTER_DRAFT_TAG, "Selected country: $selectedCountry")
        android.util.Log.d(FILTER_DRAFT_TAG, "Selected region: $selectedRegion")

        filterPreferences.saveDraftFilters(draftFilters)
    }

    private fun loadDraftFilters() {
        val draftFilters = filterPreferences.getDraftFilters()

        android.util.Log.d(FILTER_DRAFT_TAG, "Loading draft filters: $draftFilters")
        android.util.Log.d(FILTER_DRAFT_TAG, "Checkbox state in draft: ${draftFilters.hideWithoutSalary}")

        if (draftFilters.isAnyFilterApplied()) {
            updateUIWithDraftFilters(draftFilters)
            hasUnsavedChanges = true
        } else {
            loadSavedFilters()
        }
    }

    private fun clearAllFields() {
        binding.editJobLocation.text?.clear()
        selectedCountry = null
        selectedRegion = null
        updateIconAndState(binding.jobLocation, "")

        binding.editIndustry.text?.clear()
        updateIconAndState(binding.industry, "")
        selectedIndustry = null

        binding.editSalary.text?.clear()
        binding.clearIcon.visibility = View.GONE

        binding.checkbox.isChecked = false

        updateButtonsVisibility()

        filterPreferences.clearDraftFilters()
        viewModel.clearFilters()

        setFragmentResult(
            FILTER_RESULT_KEY,
            Bundle().apply {
                putBoolean(FILTERS_APPLIED_KEY, true)
            }
        )

        Toast.makeText(requireContext(), "Фильтры сброшены", Toast.LENGTH_SHORT).show()
    }

    private fun updateIconAndState(layout: TextInputLayout, text: String) {
        if (text.isEmpty()) {
            layout.endIconDrawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.arrow_right
            )
            layout.isSelected = false
            layout.isActivated = false
        } else {
            layout.endIconDrawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.clear_icon
            )
            layout.isSelected = true
            layout.isActivated = true
        }
    }

    private fun navigateOrClear(
        editText: TextInputEditText,
        layout: TextInputLayout,
        field: String
    ) {
        if (editText.text.isNullOrEmpty()) {
            when (field) {
                JOB_LOCATION_FIELD -> {
                    findNavController().navigate(
                        R.id.action_filterSettingsFragment_to_chooseWorkPlaceFragment
                    )
                }
                INDUSTRY_FIELD -> findNavController().navigate(
                    R.id.action_filterSettingsFragment_to_chooseIndustryFragment
                )
            }
        } else {
            editText.text?.clear()
            updateIconAndState(layout, "")
            when (field) {
                INDUSTRY_FIELD -> {
                    selectedIndustry = null
                    saveDraftFilters()
                }
                JOB_LOCATION_FIELD -> {
                    selectedCountry = null
                    selectedRegion = null
                    saveDraftFilters()
                }
            }
            updateButtonsVisibility()
        }
    }

    private fun closeKeyboard(view: View) {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
