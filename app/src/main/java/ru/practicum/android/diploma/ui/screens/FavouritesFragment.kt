package ru.practicum.android.diploma.ui.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFavoritesBinding

class FavouritesFragment : Fragment(R.layout.fragment_favorites) {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFavoritesBinding.bind(view)

//        // Переход на TeamFragment
//        binding.buttonToTeam.setOnClickListener {
//            findNavController().navigate(R.id.action_favouritesFragment_to_teamFragment)
//        }
//
//        // Переход на SearchFragment
//        binding.buttonToSearch.setOnClickListener {
//            findNavController().navigate(R.id.action_favouritesFragment_to_searchFragment)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
