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
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.dto.filterdto.FilterAreaDto
import ru.practicum.android.diploma.data.repositories.AreasRepository
import ru.practicum.android.diploma.databinding.FragmentChooseregionBinding
import ru.practicum.android.diploma.presentation.filter.adapter.RegionAdapter

class ChooseRegionFragment : Fragment(R.layout.fragment_chooseregion) {

    private var _binding: FragmentChooseregionBinding? = null
    private val binding get() = _binding!!
    private val repository: AreasRepository by inject()

    private val adapter: RegionAdapter by lazy {
        RegionAdapter(emptyList()) { region ->
            onRegionSelected(region)
        }
    }

    private var fullRegionList: List<FilterAreaDto> = emptyList()
    private var selectedCountryId: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChooseregionBinding.bind(view)

        selectedCountryId = arguments?.getInt("country_id")?.takeIf { it != -1 }

        setupClickListeners()
        setupRecyclerView()
        loadRegions()
        setupSearch()
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
            val query = text?.toString().orEmpty().lowercase()
            val filtered = if (query.isEmpty()) fullRegionList
            else fullRegionList.filter { it.name.lowercase().contains(query) }
            adapter.updateData(filtered)
        }
    }

    private fun loadRegions() {
        lifecycleScope.launch {
            val regions = if (selectedCountryId != null) {
                repository.getRegionsByCountry(selectedCountryId!!).orEmpty()
            } else {
                repository.getAllRegions().orEmpty()
            }

            fullRegionList = regions
            adapter.updateData(regions)
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
            // если страна не выбрана или country_id не был передан
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
