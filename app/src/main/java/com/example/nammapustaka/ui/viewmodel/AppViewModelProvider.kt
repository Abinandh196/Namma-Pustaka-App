package com.example.nammapustaka.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nammapustaka.data.AppDatabase
import com.example.nammapustaka.data.repository.LibraryRepository

import com.example.nammapustaka.data.UserManager

class AppViewModelProvider(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(application)
        val repository = LibraryRepository(database.bookDao(), database.studentDao(), database.libraryDao())
        val userManager = UserManager(application)
        
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, userManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
