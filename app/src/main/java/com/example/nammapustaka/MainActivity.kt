package com.example.nammapustaka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.nammapustaka.ui.navigation.AppNavigation
import com.example.nammapustaka.ui.theme.NammaPustakaTheme
import com.example.nammapustaka.ui.viewmodel.AppViewModelProvider
import com.example.nammapustaka.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val viewModelFactory = AppViewModelProvider(application)
        val viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            
            NammaPustakaTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}
