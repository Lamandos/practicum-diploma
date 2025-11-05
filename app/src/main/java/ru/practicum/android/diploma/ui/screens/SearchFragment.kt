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
import ru.practicum.android.diploma.presentation.search.viewmodel.SearchState
import ru.practicum.android.diploma.presentation.search.viewmodel.SearchViewModel

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()
    private var searchJob: Job? = null
    private val adapter: SearchVacancyAdapter by lazy {
        SearchVacancyAdapter { vacancy ->
            val action = SearchFragmentDirections.actionSearchFragmentToVacancyFragment2(vacancy.id)
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

        observeViewModel()
        setupSearchField()
        setupClearIcon()
    }

    private fun observeViewModel() {
        viewModel.searchState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchState.Idle -> updateUI(emptyList(), isSearchActive = false)
                is SearchState.Loading -> updateUI(emptyList(), isSearchActive = true, isLoading = true)
                is SearchState.Success -> updateUI(state.vacancies, isSearchActive = true)
                is SearchState.Empty -> updateUI(emptyList(), isSearchActive = true)
            }
        }
    }

    private fun setupSearchField() {
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
                    viewModel.resetSearch()
                    return
                }

                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.setLoading()
                    delay(SEARCH_DEBOUNCE_MS)
                    viewModel.searchVacancies(searchText)
                }
            }
        })

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.searchField.text.toString()
                if (query.isNotBlank()) {
                    viewModel.setLoading()
                    viewModel.searchVacancies(query)
                }
                closeKeyboard(binding.searchField)
                true
            } else {
                false
            }
        }
    }

    private fun setupClearIcon() {
        binding.clearIcon.setOnClickListener {
            binding.searchField.text?.clear()
            binding.searchField.isCursorVisible = true
            binding.clearIcon.visibility = View.GONE
            binding.searchIcon.visibility = View.VISIBLE
            viewModel.resetSearch()
        }
    }

    private fun updateUI(
        vacancies: List<Vacancy>,
        isSearchActive: Boolean,
        isLoading: Boolean = false
    ) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        when {
            isLoading -> {
                binding.searchStartPic.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.msgText.visibility = View.GONE
            }
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
