package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentChoosecountryBinding
import ru.practicum.android.diploma.domain.models.vacancy.Country
import ru.practicum.android.diploma.presentation.filter.adapter.CountryAdapter
import ru.practicum.android.diploma.presentation.filter.viewmodel.ChooseCountryViewModel

class ChooseCountryFragment : Fragment(R.layout.fragment_choosecountry) {

    private var _binding: FragmentChoosecountryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChooseCountryViewModel by viewModel()
    private val adapter: CountryAdapter by lazy {
        CountryAdapter(emptyList()) { country -> onCountrySelected(country) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChoosecountryBinding.bind(view)

        setupRecyclerView()
        observeViewModel()
        setupClickListeners()

        viewModel.loadCountries()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.countries.observe(viewLifecycleOwner) { countries ->
            adapter.updateData(countries)
        }

        viewModel.error.observe(viewLifecycleOwner) { showError ->
            binding.loadErrorLayout.visibility = if (showError) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (showError) View.GONE else View.VISIBLE
        }
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener { findNavController().popBackStack() }
    }

    private fun onCountrySelected(country: Country) {
        parentFragmentManager.setFragmentResult(
            "country_request",
            Bundle().apply { putParcelable("country", country) }
        )
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
