package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentTeamBinding

class TeamFragment : Fragment(R.layout.fragment_team) {

    private var _binding: FragmentTeamBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTeamBinding.bind(view)

        // Переход на FavouritesFragment
        binding.buttonToFavourites.setOnClickListener {
            findNavController().navigate(R.id.action_teamFragment_to_favouritesFragment)
        }

        // Переход на SearchFragment
        binding.buttonToSearch.setOnClickListener {
            findNavController().navigate(R.id.action_teamFragment_to_searchFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
