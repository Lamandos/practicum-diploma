package ru.practicum.android.diploma.ui.root

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.practicum.android.diploma.R

class RootActivity : AppCompatActivity() {

    private val navController: NavController by lazy {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
    }

    private val bottomNavigationView: BottomNavigationView by lazy {
        findViewById(R.id.bottom_nav)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        NavigationUI.setupWithNavController(bottomNavigationView, navController)
        bottomNavigationView.itemBackground =
            ContextCompat.getDrawable(this, android.R.color.transparent)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.filterSettingsFragment, R.id.chooseRegionFragment, R.id.chooseCountryFragment,
                R.id.chooseIndustryFragment, R.id.chooseWorkPlaceFragment, R.id.vacancyFragment2 -> {
                    hideBottomNavigationView()
                }
                else -> {
                    showBottomNavigationView()
                }
            }
        }
    }

    private fun hideBottomNavigationView() {
        bottomNavigationView.visibility = View.GONE
    }

    private fun showBottomNavigationView() {
        bottomNavigationView.visibility = View.VISIBLE
    }
}
