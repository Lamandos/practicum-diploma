package ru.practicum.android.diploma.ui.root

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.practicum.android.diploma.R
import java.lang.ClassCastException
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.NullPointerException

class RootActivity : AppCompatActivity() {

    private val navController: NavController by lazy {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
    }

    private val bottomNavigationView: BottomNavigationView by lazy {
        findViewById(R.id.bottom_nav)
    }

    companion object {
        private const val TAG = "BottomNav"
        private const val FONT_FAMILY = "ys_display_medium"
        private const val DELAY_SHORT = 50L
        private const val DELAY_MEDIUM = 100L
        private const val DELAY_LONG = 150L
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

        setupBoldTextRemover()

        // Первоначальное удаление жирности
        bottomNavigationView.postDelayed({
            removeBoldTextForcefully()
        }, DELAY_MEDIUM)

        setupNavigationListener()
    }

    private fun setupNavigationListener() {
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

    private fun setupBoldTextRemover() {
        // Слушатель изменений выбора
        bottomNavigationView.setOnItemSelectedListener { item ->
            // Даем навигации обработать клик
            NavigationUI.onNavDestinationSelected(item, navController)

            // Принудительно убираем жирность после задержки
            bottomNavigationView.postDelayed({
                removeBoldTextForcefully()
            }, DELAY_SHORT)

            true
        }

        // Слушатель повторного выбора
        bottomNavigationView.setOnItemReselectedListener { item ->
            removeBoldTextForcefully()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun removeBoldTextForcefully() {
        try {
            val menuView = bottomNavigationView.getChildAt(0) as? BottomNavigationMenuView
            processMenuView(menuView)
        } catch (e: ClassCastException) {
            Log.e(TAG, "Class cast exception in removeBoldTextForcefully", e)
        } catch (e: NullPointerException) {
            Log.e(TAG, "Null pointer exception in removeBoldTextForcefully", e)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Illegal state exception in removeBoldTextForcefully", e)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun processMenuView(menuView: BottomNavigationMenuView?) {
        menuView?.let { menu ->
            for (i in 0 until menu.childCount) {
                val itemView = menu.getChildAt(i) as? BottomNavigationItemView
                processItemView(itemView)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun processItemView(itemView: BottomNavigationItemView?) {
        itemView?.let { item ->
            removeBoldFromItemView(item)
            findAndRemoveBoldFromView(item)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun removeBoldFromItemView(itemView: BottomNavigationItemView) {
        try {
            // Большой текст (активный)
            val largeLabel = itemView.findViewById<TextView>(
                com.google.android.material.R.id.navigation_bar_item_large_label_view
            )
            // Малый текст (неактивный)
            val smallLabel = itemView.findViewById<TextView>(
                com.google.android.material.R.id.navigation_bar_item_small_label_view
            )

            applyNormalTypeface(largeLabel)
            applyNormalTypeface(smallLabel)
        } catch (e: NullPointerException) {
            Log.e(TAG, "Null pointer exception in removeBoldFromItemView", e)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Illegal argument exception in removeBoldFromItemView", e)
        }
    }

    private fun applyNormalTypeface(textView: TextView?) {
        textView?.let {
            it.setTypeface(Typeface.create(FONT_FAMILY, Typeface.NORMAL), Typeface.NORMAL)
            it.paint.isFakeBoldText = false
        }
    }

    private fun findAndRemoveBoldFromView(view: View) {
        try {
            when (view) {
                is ViewGroup -> processViewGroup(view)
                is TextView -> applyNormalTypeface(view)
            }
        } catch (e: NullPointerException) {
            Log.e(TAG, "Null pointer exception in findAndRemoveBoldFromView", e)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Illegal state exception in findAndRemoveBoldFromView", e)
        }
    }

    private fun processViewGroup(viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            findAndRemoveBoldFromView(viewGroup.getChildAt(i))
        }
    }

    private fun hideBottomNavigationView() {
        bottomNavigationView.visibility = View.GONE
    }

    private fun showBottomNavigationView() {
        bottomNavigationView.visibility = View.VISIBLE
        // Убираем жирность при каждом показе
        bottomNavigationView.postDelayed({
            removeBoldTextForcefully()
        }, DELAY_MEDIUM)
    }

    override fun onResume() {
        super.onResume()
        // Убираем жирность при возвращении в активность
        bottomNavigationView.postDelayed({
            removeBoldTextForcefully()
        }, DELAY_LONG)
    }
}
