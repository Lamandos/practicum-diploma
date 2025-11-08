package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentChooseregionBinding

class ChooseRegionFragment : Fragment(R.layout.fragment_chooseregion) {
    private var _binding: FragmentChooseregionBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChooseregionBinding.bind(view)
        setupClickListeners()
    }
    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
