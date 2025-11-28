package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
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
            val action = SearchFragmentDirections
                .actionSearchFragmentToVacancyFragment2(vacancyId = vacancy.id)
            findNavController().navigate(action)
        }
    }

    private val uiStateManager = SearchUiStateManager()

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 2000L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        binding.filterBtn.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_filterSettingsFragment)
        }

        setupFilterButton()
        setupRecyclerView()
        observeViewModel()
        setupSearchField()
        setupClearIcon()
    }

    override fun onResume() {
        super.onResume()
        updateFilterButtonState()
    }

    private fun setupFilterButton() {
        parentFragmentManager.setFragmentResultListener("filter_result", viewLifecycleOwner) { requestKey, bundle ->
            if (requestKey == "filter_result") {
                val filtersApplied = bundle.getBoolean("filters_applied", false)
                updateFilterButtonAppearance(filtersApplied)

                if (filtersApplied) {
                    val currentQuery = binding.searchField.text.toString()
                    if (currentQuery.isNotBlank()) {
                        viewModel.refreshSearchWithCurrentFilters()
                    }
                    Toast.makeText(requireContext(), "Фильтры применены", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateFilterButtonState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.checkFiltersApplied()
        }
    }

    private fun updateFilterButtonAppearance(isFilterApplied: Boolean) {
        if (isFilterApplied) {
            binding.filterBtn.setImageResource(R.drawable.trailing_fill_icon)
        } else {
            binding.filterBtn.setImageResource(R.drawable.trailing_icon)
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount
                    if (lastVisibleItem >= totalItemCount - 1) {
                        viewModel.loadNextPage()
                    }
                }
            }
        })
    }

    private fun observeViewModel() {
        viewModel.searchState.observe(viewLifecycleOwner) { state ->
            handleSearchState(state)
        }
        viewModel.isErrorToastShown.observe(viewLifecycleOwner) { isErrorToastShown ->
            handleErrorToast(isErrorToastShown)
        }
        viewModel.showLoadingState.observe(viewLifecycleOwner) { showLoading ->
            adapter.showLoading(showLoading)
        }
        // ДОБАВЛЯЕМ наблюдение за состоянием фильтров
        viewModel.isFilterApplied.observe(viewLifecycleOwner) { isApplied ->
            updateFilterButtonAppearance(isApplied)
        }
    }

    private fun handleSearchState(state: SearchState) {
        uiStateManager.handleSearchState(state, binding, adapter, viewModel, requireContext())
    }

    private fun handleErrorToast(isErrorToastShown: Boolean) {
        if (isErrorToastShown) {
            viewModel.errorMessage.value?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSearchField() {
        binding.searchField.addTextChangedListener(createTextWatcher())
        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            handleEditorAction(actionId)
        }
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                binding.searchField.isCursorVisible = !s.isNullOrEmpty()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handleTextChanged(s.toString())
            }
        }
    }

    private fun handleTextChanged(searchText: String) {
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

    private fun handleEditorAction(actionId: Int): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            val query = binding.searchField.text.toString()
            if (query.isNotBlank()) {
                viewModel.setLoading()
                viewModel.searchVacancies(query)
            }
            closeKeyboard(binding.searchField)
            return true
        }
        return false
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
