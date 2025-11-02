package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.presentation.search.viewmodel.SearchViewModel

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private var searchText = ""
    private var searchJob: Job? = null

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 2000L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        viewModel.vacancies.observe(viewLifecycleOwner, Observer { vacancies ->
            Log.d("SearchFragment", "Получили вакансии: ${vacancies.map { it.name }}")
            // TODO: обновить RecyclerView
        })

        binding.searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()

                binding.clearIcon.visibility = clearIconVisibility(s)
                binding.searchIcon.visibility = searchIconVisibility(s)

                binding.searchField.isCursorVisible = false

                searchJob?.cancel()

                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(SEARCH_DEBOUNCE_MS)
                    if (searchText.isNotBlank()) {
                        viewModel.resetSearch()
                        viewModel.searchVacancies(searchText)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    binding.searchField.isCursorVisible = true
                }
            }
        })

        binding.clearIcon.setOnClickListener {
            binding.searchField.text?.clear()
            binding.searchField.isCursorVisible = true
            binding.clearIcon.visibility = View.GONE
            binding.searchIcon.visibility = View.VISIBLE
            viewModel.resetSearch()
        }

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.resetSearch()
                viewModel.searchVacancies(searchText)
                closeKeyboard(binding.searchField)
                binding.searchField.isCursorVisible = true
                true
            } else {
                false
            }
        }
    }

    private fun clearIconVisibility(s: CharSequence?): Int =
        if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

    private fun searchIconVisibility(s: CharSequence?): Int =
        if (s.isNullOrEmpty()) View.VISIBLE else View.GONE

    private fun closeKeyboard(view: View) {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchJob?.cancel()
    }
}
