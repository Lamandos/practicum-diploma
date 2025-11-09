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
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterSettingsBinding

class FilterSettingsFragment : Fragment(R.layout.fragment_filter_settings) {

    private var _binding: FragmentFilterSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFilterSettingsBinding.bind(view)

        setupClickListeners()
        setupTextWatchers()
        updateButtonsVisibility()
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
        setFragmentResult(
            "filter_result",
            Bundle().apply {
                putBoolean("filters_applied", true)
            }
        )
        findNavController().navigateUp()
    }

    private fun clearAllFields() {
        binding.editJobLocation.text?.clear()
        updateIconAndState(binding.jobLocation, "")

        binding.editIndustry.text?.clear()
        updateIconAndState(binding.industry, "")

        binding.editSalary.text?.clear()
        binding.clearIcon.visibility = View.GONE

        binding.checkbox.isChecked = false

        updateButtonsVisibility()

        Toast.makeText(requireContext(), "Фильтры сброшены", Toast.LENGTH_SHORT).show()
    }

    private fun returnToSearchWithoutSaving() {
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
                "jobLocation" -> findNavController().navigate(
                    R.id.action_filterSettingsFragment_to_chooseWorkPlaceFragment
                )
                "industry" -> findNavController().navigate(R.id.action_filterSettingsFragment_to_chooseIndustryFragment)
            }
        } else {
            editText.text?.clear()
            updateIconAndState(layout, "")
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
