package com.mrjosh.homepi.fragments

import android.os.Bundle
import android.view.View
import com.mrjosh.homepi.R
import androidx.fragment.app.Fragment
import android.content.SharedPreferences

open class BaseFragment : Fragment() {

    private var currentTheme: String? = null
    private var prefs: SharedPreferences? = null
    private val prefsFileName = "com.mrjosh.homepi.prefs"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = context?.getSharedPreferences(prefsFileName, 0)
        val theme = prefs?.getString("current_theme", "dark")
        updateCurrentTheme(theme)
    }

    private fun updateCurrentTheme(currentTheme: String?) {
        this.currentTheme = currentTheme
        prefs!!.edit().putString("current_theme", currentTheme).apply()
        setCurrentTheme()
    }

    private fun setCurrentTheme() {
        if (currentTheme == "dark") {
            activity?.setTheme(R.style.AppTheme_Dark)
        } else {
            activity?.setTheme(R.style.AppTheme_Default)
        }
    }

    override fun onResume() {
        super.onResume()
        val theme = prefs!!.getString("current_theme", "dark")
        if (currentTheme != theme) {
            activity?.recreate()
        }
    }
}