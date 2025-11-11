package ru.practicum.android.diploma.ui.screens.filter

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SalaryFieldManager(
    private val salaryEditText: TextInputEditText,
    private val salaryLayout: TextInputLayout,
    private val clearIcon: View,
    private val onSalaryChanged: () -> Unit
) {

    fun setupSalaryField() {
        setupSalaryInputFilter()
        setupSalaryTextWatcher()
        setupSalaryFocusBehavior()
        setupSalaryEditorDoneAction()
        setupSalaryClearIcon()
    }

    private fun setupSalaryInputFilter() {
        salaryEditText.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("[0-9]+"))) source else ""
        })
    }

    private fun setupSalaryTextWatcher() {
        salaryEditText.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                    if (s.toString() == "0") {
                        clearIcon.visibility = View.GONE
                        Toast.makeText(
                            salaryEditText.context,
                            "Некорректное значение",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    onSalaryChanged()
                }
            }
        )
    }

    private fun setupSalaryFocusBehavior() {
        salaryEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && !salaryEditText.text.isNullOrEmpty()) {
                clearIcon.visibility = View.VISIBLE
            }
        }
    }

    private fun setupSalaryEditorDoneAction() {
        salaryEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                clearIcon.visibility = View.GONE
                closeKeyboard(salaryEditText)
                salaryEditText.clearFocus()
                salaryLayout.clearFocus()
                true
            } else {
                false
            }
        }
    }

    private fun setupSalaryClearIcon() {
        clearIcon.setOnClickListener {
            salaryEditText.text?.clear()
            onSalaryChanged()
        }
    }

    private fun closeKeyboard(view: View) {
        val imm = view.context.getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getSalaryText(): String? = salaryEditText.text?.toString()

    fun clearSalary() {
        salaryEditText.text?.clear()
    }
}
