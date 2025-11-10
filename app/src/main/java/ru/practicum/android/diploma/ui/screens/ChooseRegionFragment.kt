package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentChooseregionBinding
import ru.practicum.android.diploma.domain.models.filtermodels.Region
import ru.practicum.android.diploma.domain.models.vacancy.Country
import ru.practicum.android.diploma.presentation.filter.adapter.RegionAdapter
import ru.practicum.android.diploma.presentation.filter.viewmodel.ChooseRegionViewModel
import ru.practicum.android.diploma.presentation.filter.viewmodel.RegionError

class ChooseRegionFragment : Fragment(R.layout.fragment_chooseregion) {

    private var _binding: FragmentChooseregionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChooseRegionViewModel by viewModel()
    private val adapter: RegionAdapter by lazy {
        RegionAdapter(emptyList()) { region -> onRegionSelected(region) }
    }

    private var selectedCountry: Country? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChooseregionBinding.bind(view)

        selectedCountry = arguments?.getParcelable("country")

        setupRecyclerView()
        observeViewModel()
        setupClickListeners()
        setupSearch()

        if (selectedCountry != null) {
            viewModel.loadRegions(selectedCountry!!)
        } else {
            viewModel.loadAllRegions()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.filteredRegions.observe(viewLifecycleOwner) { regions ->
            adapter.updateData(regions)
            binding.noRegionError.visibility = View.GONE
            binding.noRegionListError.visibility = View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.noRegionError.visibility = View.GONE
            binding.noRegionListError.visibility = View.GONE

            when (error) {
                RegionError.NO_RESULTS -> binding.noRegionError.visibility = View.VISIBLE
                RegionError.LOAD_FAILED -> binding.noRegionListError.visibility = View.VISIBLE
                null -> {  }
            }
        }
    }

    private fun setupSearch() {
        binding.searchField.addTextChangedListener { text ->
            val query = text?.toString().orEmpty()
            viewModel.filterRegions(query)

            if (query.isNotEmpty()) {
                binding.clearIcon.visibility = View.VISIBLE
                binding.searchIcon.visibility = View.GONE
            } else {
                binding.clearIcon.visibility = View.GONE
                binding.searchIcon.visibility = View.VISIBLE
            }
        }

        binding.clearIcon.setOnClickListener {
            binding.searchField.text.clear()
            binding.searchField.clearFocus()
            viewModel.filterRegions("")
            binding.clearIcon.visibility = View.GONE
            binding.searchIcon.visibility = View.VISIBLE
        }
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener { findNavController().popBackStack() }
    }

    private fun onRegionSelected(region: Region) {
        parentFragmentManager.setFragmentResult(
            "region_request",
            Bundle().apply { putParcelable("region", region) }
        )
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
