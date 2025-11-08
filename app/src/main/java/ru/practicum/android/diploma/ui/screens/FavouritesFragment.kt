package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFavoritesBinding
import ru.practicum.android.diploma.domain.models.vacancydetails.VacancyDetails
import ru.practicum.android.diploma.presentation.favorites.adapter.VacancyAdapter
import ru.practicum.android.diploma.presentation.favorites.state.FavoritesState
import ru.practicum.android.diploma.presentation.favorites.viewmodel.FavoritesViewModel

class FavouritesFragment : Fragment(R.layout.fragment_favorites) {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModel()

    private val adapter: VacancyAdapter by lazy {
        VacancyAdapter(
            onItemClick = { vacancy ->
                navigateToVacancyDetails(vacancy.id)
            },
            context = requireContext()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoritesBinding.bind(view)

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavouritesFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.favoritesState.observe(viewLifecycleOwner) { state ->
            handleState(state)
        }
    }

    private fun handleState(state: FavoritesState) {
        when (state) {
            is FavoritesState.Loading -> showLoading()
            is FavoritesState.Success -> showVacancies(state.vacancies)
            is FavoritesState.Empty -> showEmptyState()
            is FavoritesState.Error -> showErrorState()
        }
    }

    private fun showLoading() {
        binding.recyclerView.visibility = View.GONE
    }

    private fun navigateToVacancyDetails(vacancyId: String) {
        val bundle = Bundle().apply {
            putString("vacancyId", vacancyId)
            putBoolean("fromFavorites", true)
        }
        findNavController().navigate(R.id.action_favouritesFragment_to_vacancyFragment2, bundle)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavorites()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showVacancies(vacancies: List<VacancyDetails>) {
        binding.recyclerView.visibility = View.VISIBLE
        binding.noVacError.visibility = View.GONE
        binding.noNetError.visibility = View.GONE

        adapter.submitList(vacancies)
    }

    private fun showEmptyState() {
        binding.recyclerView.visibility = View.GONE
        binding.noVacError.visibility = View.VISIBLE
        binding.noNetError.visibility = View.GONE
    }

    private fun showErrorState() {
        binding.recyclerView.visibility = View.GONE
        binding.noVacError.visibility = View.GONE
        binding.noNetError.visibility = View.VISIBLE
    }
}
