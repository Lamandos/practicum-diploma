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
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.dto.filterdto.FilterIndustryDto
import ru.practicum.android.diploma.databinding.FragmentFilterSettingsBinding
import ru.practicum.android.diploma.domain.models.filtermodels.Industry
import ru.practicum.android.diploma.domain.models.filtermodels.Region
import ru.practicum.android.diploma.domain.models.filtermodels.VacancyFilters
import ru.practicum.android.diploma.domain.models.vacancy.Country
import ru.practicum.android.diploma.presentation.filter.viewmodel.FilterViewModel
import ru.practicum.android.diploma.ui.model.FilterIndustryUI

class FilterSettingsFragment : Fragment(R.layout.fragment_filter_settings) {

    private var _binding: FragmentFilterSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FilterViewModel by viewModel()

    private var selectedIndustry: FilterIndustryUI? = null
    private var selectedCountry: Country? = null
    private var selectedRegion: Region? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFilterSettingsBinding.bind(view)

        setupFragmentResultListeners()
        setupClickListeners()
        setupTextWatchers()
        observeViewModel()
        loadSavedFilters()
    }

    private fun observeViewModel() {
        viewModel.filtersState.observe(viewLifecycleOwner) { filters ->
            updateUIWithFilters(filters)
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
            binding.editIndustry.setText(industry.name)
            // КОНВЕРТИРУЕМ Industry в FilterIndustryUI
            selectedIndustry = FilterIndustryUI(
                id = industry.id.toIntOrNull() ?: 0,
                name = industry.name
            )
            updateIconAndState(binding.industry, industry.name)
        }

        // Обновляем зарплату
        filters.salary?.let { salary ->
            binding.editSalary.setText(salary.toString())
        }

        // Обновляем регион
        filters.region?.let { region ->
            selectedRegion = if (region.name.isNotEmpty()) region else null
            selectedCountry = region.country

            val locationText = when {
                region.country != null && region.name.isNotBlank() ->
                    "${region.country.name}, ${region.name}"

                region.country != null ->
                    region.country.name

                else -> region.name
            }

            binding.editJobLocation.setText(locationText)
            updateIconAndState(binding.jobLocation, locationText)
        }

        // Обновляем чекбокс - ВАЖНОЕ ИСПРАВЛЕНИЕ
        binding.checkbox.isChecked = filters.hideWithoutSalary ?: false

        updateButtonsVisibility()
    }

    private fun setupFragmentResultListeners() {
        setFragmentResultListener("industry_result") { _, bundle ->
            bundle.getParcelable<FilterIndustryUI>("selected_industry")?.let { industry ->
                selectedIndustry = industry
                binding.editIndustry.setText(industry.name)
                updateIconAndState(binding.industry, industry.name)
                updateButtonsVisibility()
            }
        }

        setFragmentResultListener("workplace_result") { _, bundle ->
            selectedCountry = bundle.getParcelable("country")
            selectedRegion = bundle.getParcelable("region")

            val text = when {
                selectedRegion != null && selectedCountry != null ->
                    "${selectedCountry!!.name}, ${selectedRegion!!.name}"

                selectedCountry != null ->
                    selectedCountry!!.name

                else -> ""
            }

            binding.editJobLocation.setText(text)
            updateIconAndState(binding.jobLocation, text)

            updateButtonsVisibility()
        }
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            returnToSearchWithoutSaving()
        }

        binding.btnAccept.setOnClickListener {
            applyFiltersAndReturn()
        }

        binding.btnDeny.setOnClickListener {
            clearAllFields()
        }

        binding.clearIcon.setOnClickListener {
            binding.editSalary.text?.clear()
        }
    }

    private fun setupTextWatchers() {
        val jobLocationLayout: TextInputLayout = binding.jobLocation
        val jobLocationEditText: TextInputEditText = binding.editJobLocation
        val industryLayout: TextInputLayout = binding.industry
        val industryEditText: TextInputEditText = binding.editIndustry
        val salaryLayout: TextInputLayout = binding.salaryField
        val salaryEditText: TextInputEditText = binding.editSalary

        jobLocationLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
        industryLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM

        jobLocationEditText.addTextChangedListener {
            updateIconAndState(jobLocationLayout, it?.toString().orEmpty())
            updateButtonsVisibility()
        }

        industryEditText.addTextChangedListener {
            updateIconAndState(industryLayout, it?.toString().orEmpty())
            updateButtonsVisibility()
        }

        jobLocationLayout.setEndIconOnClickListener {
            navigateOrClear(jobLocationEditText, jobLocationLayout, "jobLocation")
        }

        industryLayout.setEndIconOnClickListener {
            navigateOrClear(industryEditText, industryLayout, "industry")
        }

        binding.editSalary.addTextChangedListener {
            binding.clearIcon.visibility = if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
            updateButtonsVisibility()
        }

        binding.checkbox.setOnCheckedChangeListener { _, _ ->
            updateButtonsVisibility()
        }

        updateIconAndState(jobLocationLayout, jobLocationEditText.text.toString())
        updateIconAndState(industryLayout, industryEditText.text.toString())

        setupSalaryField(salaryEditText, salaryLayout)
    }

    private fun updateButtonsVisibility() {
        val hasFilters = hasAnyFilterApplied()

        if (hasFilters) {
            binding.btnAccept.visibility = View.VISIBLE
            binding.btnDeny.visibility = View.VISIBLE
        } else {
            binding.btnAccept.visibility = View.GONE
            binding.btnDeny.visibility = View.GONE
        }
    }

    private fun hasAnyFilterApplied(): Boolean {
        return binding.editJobLocation.text?.isNotBlank() == true ||
            binding.editIndustry.text?.isNotBlank() == true ||
            binding.editSalary.text?.isNotBlank() == true ||
            binding.checkbox.isChecked
    }

    private fun applyFiltersAndReturn() {
        val filters = createFiltersFromUI()
        viewModel.updateFilters(filters)

        setFragmentResult(
            "filter_result",
            Bundle().apply {
                putBoolean("filters_applied", true)
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

        val regionToSave =
            selectedRegion ?: selectedCountry?.let { country ->
                Region(
                    id = country.id,
                    name = "",
                    country = country
                )
            }
        return VacancyFilters(
            region = regionToSave,
            industry = industry,
            salary = salary,
            hideWithoutSalary = hideWithoutSalary,
            currency = "RUB"
        )
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

        // Очищаем фильтры в ViewModel
        viewModel.clearFilters()

        Toast.makeText(requireContext(), "Фильтры сброшены", Toast.LENGTH_SHORT).show()
    }

    private fun returnToSearchWithoutSaving() {
        val currentFilters = createFiltersFromUI()
        viewModel.updateFilters(currentFilters)
        setFragmentResult(
            "filter_result",
            Bundle().apply {
                putBoolean("filters_applied", false)
            }
        )
        findNavController().navigateUp()
    }

    private fun updateIconAndState(layout: TextInputLayout, text: String) {
        if (text.isEmpty()) {
            layout.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.arrow_right)
            layout.isSelected = false
            layout.isActivated = false
        } else {
            layout.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.clear_icon)
            layout.isSelected = true
            layout.isActivated = true
        }
    }

    private fun navigateOrClear(editText: TextInputEditText, layout: TextInputLayout, field: String) {
        if (editText.text.isNullOrEmpty()) {
            when (field) {
                "jobLocation" -> {
                    if (editText.text.isNullOrEmpty()) {
                        findNavController().navigate(R.id.action_filterSettingsFragment_to_chooseWorkPlaceFragment)
                    } else {
                        editText.text?.clear()
                        updateIconAndState(layout, "")
                        updateButtonsVisibility()
                    }
                }

                "industry" -> findNavController().navigate(R.id.action_filterSettingsFragment_to_chooseIndustryFragment)
            }
        } else {
            editText.text?.clear()
            updateIconAndState(layout, "")
            when (field) {
                "industry" -> selectedIndustry = null
            }
            updateButtonsVisibility()
        }
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
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.clearIcon.visibility =
                        if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                    if (s.toString() == "0") {
                        binding.clearIcon.visibility = View.GONE
                        Toast.makeText(requireContext(), "Некорректное значение", Toast.LENGTH_LONG).show()
                    }
                }

                override fun afterTextChanged(s: Editable?) = Unit
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

    private fun setupSalaryEditorDoneAction(editText: TextInputEditText, layout: TextInputLayout) {
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
        }
    }

    private fun closeKeyboard(view: View) {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
