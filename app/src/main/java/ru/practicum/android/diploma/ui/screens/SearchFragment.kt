package ru.practicum.android.diploma.ui.screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import retrofit2.HttpException
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
            val action = SearchFragmentDirections
                .actionSearchFragmentToVacancyFragment2(vacancyId = vacancy.id)
            findNavController().navigate(action)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 2000L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        setupRecyclerView()
        observeViewModel()
        setupSearchField()
        setupClearIcon()
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
            when (state) {
                is SearchState.Idle -> {
                    updateUI(emptyList(), isSearchActive = false)
                    adapter.showLoading(false)
                }
                is SearchState.Loading -> {
                    updateUI(emptyList(), isSearchActive = true, isLoading = true)
                    adapter.showLoading(false)
                }
                is SearchState.Success -> {
                    updateUI(state.vacancies, isSearchActive = true)
                }
                is SearchState.Empty -> {
                    updateUI(emptyList(), isSearchActive = true)
                    adapter.showLoading(false)
                }
                is SearchState.Error -> {
                    val hasNetwork = isNetworkAvailable(requireContext())
                    val isServerError =
                        (state.throwable as? HttpException)?.code() == 500
                    updateUI(
                        vacancies = emptyList(),
                        isSearchActive = true,
                        isLoading = false,
                        isNetworkAvailable = hasNetwork,
                        isServerError = isServerError
                    )
                    adapter.showLoading(false)
                }
            }
        }

        viewModel.isErrorToastShown.observe(viewLifecycleOwner) { isErrorToastShown ->
            if (isErrorToastShown) {
                viewModel.errorMessage.value?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.showLoadingState.observe(viewLifecycleOwner) { showLoading ->
            adapter.showLoading(showLoading)
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
        isLoading: Boolean = false,
        isNetworkAvailable: Boolean = true,
        isServerError: Boolean = false
    ) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        when {
            isLoading -> {
                binding.noNetError.visibility = View.GONE
                binding.serverError.visibility = View.GONE
                binding.noVacError.visibility = View.GONE
                binding.searchStartPic.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.msgText.visibility = View.GONE
            }
            !isNetworkAvailable -> {
                binding.noNetError.visibility = View.VISIBLE
                binding.serverError.visibility = View.GONE
                binding.noVacError.visibility = View.GONE
                binding.searchStartPic.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.msgText.visibility = View.GONE
            }
            isServerError -> {
                binding.noNetError.visibility = View.GONE
                binding.serverError.visibility = View.VISIBLE
                binding.noVacError.visibility = View.GONE
                binding.searchStartPic.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.msgText.visibility = View.GONE
            }
            vacancies.isEmpty() && isSearchActive -> {
                binding.noNetError.visibility = View.GONE
                binding.serverError.visibility = View.GONE
                binding.noVacError.visibility = View.VISIBLE
                binding.searchStartPic.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.msgText.visibility = View.VISIBLE
                binding.msgText.text = getString(R.string.no_vac_msg)
            }
            vacancies.isNotEmpty() -> {
                binding.noNetError.visibility = View.GONE
                binding.serverError.visibility = View.GONE
                binding.noVacError.visibility = View.GONE
                binding.searchStartPic.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.msgText.visibility = View.VISIBLE
                binding.msgText.text = resources.getQuantityString(
                    R.plurals.found_vac_msg,
                    viewModel.totalFoundCount,
                    viewModel.totalFoundCount
                )
                adapter.setItems(vacancies)
            }
            else -> {
                binding.noNetError.visibility = View.GONE
                binding.serverError.visibility = View.GONE
                binding.noVacError.visibility = View.GONE
                binding.searchStartPic.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.msgText.visibility = View.GONE
            }
        }
    }
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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
