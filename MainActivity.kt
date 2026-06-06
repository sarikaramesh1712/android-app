package com.example.mobileproject

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mobileproject.data.AppContainer
import com.example.mobileproject.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppContainer.initialize(this)
        startBackgroundDataFetch()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.navigation_dashboard
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    private fun startBackgroundDataFetch() {
        val repository = AppContainer.getRepository()

        applicationScope.launch {
            try {
                println("MainActivity: Checking if data needs to be fetched...")
                val shouldFetch = repository.shouldFetchData()

                if (shouldFetch) {
                    println("MainActivity: Starting background data fetch...")
                    val result = repository.fetchAndStoreAllSignals()

                    if (result.isSuccess) {
                        println("MainActivity: Background data fetch completed successfully")
                    } else {
                        println("MainActivity: Background data fetch failed: ${result.exceptionOrNull()?.message}")
                    }
                } else {
                    println("MainActivity: Data already available, no fetch needed")
                }
            } catch (e: Exception) {
                println("MainActivity: Error in background data fetch: ${e.message}")
            }
        }
    }

    // Create application-level coroutine scope
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}