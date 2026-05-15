package com.example.nammapustaka.data

import android.content.Context
import android.content.SharedPreferences

class UserManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("namma_pustaka_prefs", Context.MODE_PRIVATE)

    fun loginUser(studentId: Int) {
        prefs.edit().putInt("LOGGED_IN_USER_ID", studentId).apply()
    }

    fun getLoggedInUserId(): Int? {
        val id = prefs.getInt("LOGGED_IN_USER_ID", -1)
        return if (id == -1) null else id
    }

    fun logout() {
        prefs.edit().remove("LOGGED_IN_USER_ID").apply()
    }

    fun setDarkMode(isDark: Boolean) {
        prefs.edit().putBoolean("DARK_MODE", isDark).apply()
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean("DARK_MODE", false)
    }
}
