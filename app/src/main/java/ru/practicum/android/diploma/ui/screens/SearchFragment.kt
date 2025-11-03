package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.domain.models.vacancy.Vacancy
import ru.practicum.android.diploma.presentation.search.adapter.SearchVacancyAdapter
import ru.practicum.android.diploma.presentation.search.viewmodel.SearchViewModel

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()
    private var searchJob: Job? = null
    private val adapter: SearchVacancyAdapter by lazy {
        SearchVacancyAdapter { vacancy ->
            val action = SearchFragmentDirections.actionSearchFragmentToVacancyFragment2()
            findNavController().navigate(action)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 2000L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.vacancies.value?.let { vacancies ->
            updateUI(
                vacancies,
                isSearchActive = viewModel.searchQuery.value?.isNotEmpty() == true
            )
        }

        viewModel.vacancies.observe(viewLifecycleOwner) { vacancies ->
            val isSearchActive = viewModel.searchQuery.value?.isNotEmpty() == true
            updateUI(vacancies, isSearchActive)
        }

        viewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            if (binding.searchField.text.toString() != query) {
                binding.searchField.setText(query)
            }
        }

        binding.searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                binding.searchField.isCursorVisible = !s.isNullOrEmpty()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString()
                binding.clearIcon.visibility = if (searchText.isEmpty()) View.GONE else View.VISIBLE
                binding.searchIcon.visibility = if (searchText.isEmpty()) View.VISIBLE else View.GONE

                searchJob?.cancel()
                if (searchText.isBlank()) {
                    updateUI(emptyList(), isSearchActive = false)
                    return
                }


                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(SEARCH_DEBOUNCE_MS)
                    if (viewModel.searchQuery.value != searchText) {
                        viewModel.resetSearch()
                        viewModel.searchVacancies(searchText)
                    }
                }
            }
        })

        binding.clearIcon.setOnClickListener {
            binding.searchField.text?.clear()
            binding.searchField.isCursorVisible = true
            binding.clearIcon.visibility = View.GONE
            binding.searchIcon.visibility = View.VISIBLE

            viewModel.resetSearch()
            updateUI(emptyList(), isSearchActive = false)
        }

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.searchField.text.toString()
                if (query.isNotBlank()) {
                    viewModel.resetSearch()
                    viewModel.searchVacancies(query)
                }
                closeKeyboard(binding.searchField)
                true
            } else {
                false
            }
        }
    }
    private fun updateUI(vacancies: List<Vacancy>, isSearchActive: Boolean) {
        when {
            vacancies.isEmpty() && isSearchActive -> {
                binding.searchStartPic.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.msgText.visibility = View.VISIBLE
                binding.msgText.text = getString(R.string.no_vac_msg)
            }
            vacancies.isNotEmpty() -> {
                binding.searchStartPic.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter.setItems(vacancies)
                binding.msgText.visibility = View.VISIBLE
                binding.msgText.text = getString(R.string.found_vac_msg, vacancies.size)
            }
            else -> {
                binding.searchStartPic.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.msgText.visibility = View.GONE
            }
        }
    }

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
