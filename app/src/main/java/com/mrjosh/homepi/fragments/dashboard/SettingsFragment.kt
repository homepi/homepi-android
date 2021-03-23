package com.mrjosh.homepi.fragments.dashboard

import android.os.Bundle
import com.mrjosh.homepi.R
import androidx.preference.Preference
import android.content.SharedPreferences
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment: PreferenceFragmentCompat() {
    private var currentTheme: String? = null
    private var prefs: SharedPreferences? = null
    private val prefsFileName = "com.mrjosh.homepi.prefs"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        prefs = context?.getSharedPreferences(prefsFileName, 0)
        val theme = prefs?.getString("current_theme", "dark")
        updateCurrentTheme(theme)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            "light_mode" -> { toggleDarkMode() }
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun toggleDarkMode() {
        when (currentTheme) {
            "dark" -> updateTheme(R.style.AppTheme_Default)
            else -> updateTheme(R.style.AppTheme_Dark)
        }
    }

    private fun updateTheme(theme: Int) {
        when (theme) {
            R.style.AppTheme_Dark -> updateCurrentTheme("dark")
            R.style.AppTheme_Default -> updateCurrentTheme("light")
        }
        requireActivity().theme?.applyStyle(theme, true)
        requireActivity().recreate()
    }

    private fun updateCurrentTheme(currentTheme: String?) {
        this.currentTheme = currentTheme
        prefs!!.edit().putString("current_theme", currentTheme).apply()
        when (currentTheme) {
            "dark" -> requireActivity().setTheme(R.style.AppTheme_Dark)
            else -> requireActivity().setTheme(R.style.AppTheme_Default)
        }
    }

    override fun onResume() {
        super.onResume()
        val theme = prefs!!.getString("current_theme", "dark")
        if (currentTheme != theme) {
            requireActivity().recreate()
        }
    }
}