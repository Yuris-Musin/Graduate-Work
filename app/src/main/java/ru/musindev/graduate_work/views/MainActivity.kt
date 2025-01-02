package ru.musindev.graduate_work.views

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ru.musindev.graduate_work.R
import ru.musindev.graduate_work.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val navController by lazy {
        NavHostFragment.findNavController(supportFragmentManager.findFragmentById(R.id.fragment_placeholder) as NavHostFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setupWithNavController(navController)

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.search -> {
                    navController.navigate(R.id.searchFragment)
                    true
                }

                R.id.about -> {
                    navController.navigate(R.id.aboutFragment)
                    true
                }

                R.id.account -> {
                    navController.navigate(R.id.accountFragment)
                    true
                }

                else -> false
            }
        }

    }
}