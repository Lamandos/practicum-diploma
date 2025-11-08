package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.repositories.AreasRepository
import ru.practicum.android.diploma.databinding.FragmentChooseregionBinding
import ru.practicum.android.diploma.presentation.filter.adapter.RegionAdapter

class ChooseRegionFragment : Fragment(R.layout.fragment_chooseregion) {

    private var _binding: FragmentChooseregionBinding? = null
    private val binding get() = _binding!!
    private val repository: AreasRepository by inject()

    private val adapter: RegionAdapter by lazy {
        RegionAdapter(emptyList()) { selectedRegion ->
            // обработка клика по региону
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChooseregionBinding.bind(view)

        setupClickListeners()
        setupRecyclerView()
        loadRegions()
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

    private fun loadRegions() {
        lifecycleScope.launch {
            val regions = repository.getAllRegions().orEmpty()
            adapter.updateData(regions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
