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

    // Флаг для отслеживания состояния View
    private var isViewCreated = false

    companion object {
        private const val SALARY_SAVE_DELAY_MS = 500L
        private const val FILTER_RESULT_KEY = "filter_result"
        private const val INDUSTRY_RESULT_KEY = "industry_result"
        private const val WORKPLACE_RESULT_KEY = "workplace_result"
        private const val FILTERS_APPLIED_KEY = "filters_applied"
        private const val SELECTED_INDUSTRY_KEY = "selected_industry"
        private const val JOB_LOCATION_FIELD = "jobLocation"
        private const val INDUSTRY_FIELD = "industry"
        private const val CURRENCY_RUB = "RUB"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFilterSettingsBinding.bind(view)
        isViewCreated = true

        setupFragmentResultListeners()
        setupClickListeners()
        setupTextWatchers()
        observeViewModel()
        loadSavedFilters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isViewCreated = false
        binding.editSalary.removeCallbacks(salarySaveRunnable)
        _binding = null
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

        // Обновляем чекбокс "Только с зарплатой"
        binding.checkbox.isChecked = filters.hideWithoutSalary ?: false

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

        updateButtonsVisibility()
    }

    private fun setupFragmentResultListeners() {
        setFragmentResultListener(INDUSTRY_RESULT_KEY) { _, bundle ->
            bundle.getParcelable<FilterIndustryUI>(SELECTED_INDUSTRY_KEY)?.let { industry ->
                selectedIndustry = industry
                binding.editIndustry.setText(industry.name)
                updateIconAndState(binding.industry, industry.name)
                updateButtonsVisibility()
                saveFiltersAutomatically() // Автосохранение при выборе отрасли
            }
        }

        setFragmentResultListener(WORKPLACE_RESULT_KEY) { _, bundle ->
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
            saveFiltersAutomatically() // Автосохранение при выборе места работы
        }
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            // Фильтры уже сохранены автоматически, просто выходим
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
            saveFiltersAutomatically() // Сохраняем при очистке зарплаты
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
            navigateOrClear(jobLocationEditText, jobLocationLayout, JOB_LOCATION_FIELD)
        }

        industryLayout.setEndIconOnClickListener {
            navigateOrClear(industryEditText, industryLayout, INDUSTRY_FIELD)
        }

        binding.editSalary.addTextChangedListener { text ->
            binding.clearIcon.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
            updateButtonsVisibility()

            // Автосохранение при изменении зарплаты (с задержкой чтобы не спамить)
            if (isViewCreated) {
                binding.editSalary.removeCallbacks(salarySaveRunnable)
                binding.editSalary.postDelayed(salarySaveRunnable, SALARY_SAVE_DELAY_MS)
            }
        }

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            updateButtonsVisibility()
            saveFiltersAutomatically() // Автосохранение при изменении чекбокса
        }

        updateIconAndState(jobLocationLayout, jobLocationEditText.text.toString())
        updateIconAndState(industryLayout, industryEditText.text.toString())

        setupSalaryField(salaryEditText, salaryLayout)
    }

    // Runnable для отложенного сохранения зарплаты
    private val salarySaveRunnable = Runnable {
        if (isViewCreated) {
            saveFiltersAutomatically()
        }
    }

    private fun saveFiltersAutomatically() {
        if (!isViewCreated) return

        viewLifecycleOwner.lifecycleScope.launch {
            val filters = createFiltersFromUI()
            viewModel.updateFilters(filters)
        }
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
        // Фильтры уже сохранены автоматически, просто выходим с флагом применения
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

        val regionToSave = selectedRegion ?: selectedCountry?.let { country ->
            Region(
                id = country.id,
                name = "", // ← только страна
                country = country
            )
        }

        return VacancyFilters(
            region = regionToSave,
            industry = industry,
            salary = salary,
            hideWithoutSalary = hideWithoutSalary,
            currency = CURRENCY_RUB
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
                JOB_LOCATION_FIELD -> {
                    findNavController().navigate(R.id.action_filterSettingsFragment_to_chooseWorkPlaceFragment)
                }
                INDUSTRY_FIELD -> findNavController().navigate(R.id.action_filterSettingsFragment_to_chooseIndustryFragment)
            }
        } else {
            editText.text?.clear()
            updateIconAndState(layout, "")
            when (field) {
                INDUSTRY_FIELD -> selectedIndustry = null
                JOB_LOCATION_FIELD -> {
                    selectedCountry = null
                    selectedRegion = null
                }
            }
            updateButtonsVisibility()
            saveFiltersAutomatically() // Сохраняем при очистке поля
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

                // Сохраняем при завершении ввода зарплаты
                saveFiltersAutomatically()

                true
            } else {
                false
            }
        }
    }

    private fun setupSalaryClearIcon(editText: TextInputEditText) {
        binding.clearIcon.setOnClickListener {
            editText.text?.clear()
            saveFiltersAutomatically() // Сохраняем при очистке зарплаты
        }
    }

    private fun closeKeyboard(view: View) {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
