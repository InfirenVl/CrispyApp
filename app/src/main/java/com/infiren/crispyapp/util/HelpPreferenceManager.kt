package com.infiren.crispyapp.util

import android.content.Context
import android.content.SharedPreferences


object HelpPreferenceManager {
    private const val PREFS_NAME = "prefs"
    private const val KEY_FIRST_LAUNCH = "first_launch"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isFirstLaunch(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunch(context: Context, isFirst: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_FIRST_LAUNCH, isFirst).apply()
    }
}