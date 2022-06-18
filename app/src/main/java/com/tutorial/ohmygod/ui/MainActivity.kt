package com.tutorial.ohmygod.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.tutorial.ohmygod.R
import com.tutorial.ohmygod.arch.NewsViewModel
import com.tutorial.ohmygod.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel:NewsViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.handleResponse()


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.breakingNews,
                R.id.savedNews,
                R.id.searchNews
            )
        )
        val fragHost = supportFragmentManager.findFragmentById(R.id.fragHost) as NavHostFragment
        navController = fragHost.findNavController()

        setupActionBarWithNavController(navController,appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.isVisible = appBarConfiguration.topLevelDestinations.contains(destination.id)

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}