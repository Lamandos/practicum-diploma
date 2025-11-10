package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.dto.filterdto.FilterAreaDto
import ru.practicum.android.diploma.data.repositories.AreasRepository
import ru.practicum.android.diploma.databinding.FragmentChooseregionBinding
import ru.practicum.android.diploma.presentation.filter.adapter.RegionAdapter
import ru.practicum.android.diploma.presentation.filter.viewmodel.ChooseRegionViewModel

class ChooseRegionFragment : Fragment(R.layout.fragment_chooseregion) {

    private var _binding: FragmentChooseregionBinding? = null
    private val binding get() = _binding!!
    private val repository: AreasRepository by inject()
    private val viewModel: ChooseRegionViewModel by viewModel()

    private val adapter: RegionAdapter by lazy {
        RegionAdapter(emptyList()) { region ->
            onRegionSelected(region)
        }
    }

    private var selectedCountryId: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChooseregionBinding.bind(view)

        selectedCountryId = arguments?.getInt("country_id")?.takeIf { it != -1 }

        setupClickListeners()
        setupRecyclerView()
        observeViewModel()
        setupSearch()

        viewModel.loadRegions(selectedCountryId)
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchField.addTextChangedListener { text ->
            val query = text?.toString().orEmpty()
            viewModel.filterRegions(query)
        }
    }

    private fun observeViewModel() {
        viewModel.filteredRegions.observe(viewLifecycleOwner) { regions ->
            adapter.updateData(regions)

            when {
                regions.isEmpty() && binding.searchField.text?.isNotEmpty() == true -> {
                    binding.recyclerView.visibility = View.GONE
                    binding.noRegionError.visibility = View.VISIBLE
                    binding.noRegionListError.visibility = View.GONE
                }

                regions.isEmpty() -> {
                    binding.recyclerView.visibility = View.GONE
                    binding.noRegionError.visibility = View.GONE
                    binding.noRegionListError.visibility = View.VISIBLE
                }

                else -> {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.noRegionError.visibility = View.GONE
                    binding.noRegionListError.visibility = View.GONE
                }
            }
        }
    }

    private fun onRegionSelected(region: FilterAreaDto) {
        parentFragmentManager.setFragmentResult(
            "region_request",
            Bundle().apply {
                putString("region_name", region.name)
                putInt("region_id", region.id)
            }
        )

        lifecycleScope.launch {
            if (selectedCountryId == null) {
                val allAreas = repository.getAllAreas().orEmpty()
                val country = allAreas.firstOrNull { it.areas.any { it.id == region.id } }
                country?.let {
                    parentFragmentManager.setFragmentResult(
                        "country_request",
                        Bundle().apply {
                            putString("country_name", it.name)
                            putInt("country_id", it.id)
                        }
                    )
                }
            }
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
