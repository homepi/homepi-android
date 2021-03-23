package com.mrjosh.homepi.activities.auth

import android.os.Bundle
import com.mrjosh.homepi.R
import com.mrjosh.homepi.activities.BaseActivity
import com.mrjosh.homepi.fragments.auth.ServerFragment

class LoginActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            ).replace(R.id.steps_layout, ServerFragment())
            .commit()
    }
}