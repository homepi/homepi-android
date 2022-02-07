package net.mrjosh.homepi.activities.auth

import android.os.Bundle
import net.mrjosh.homepi.R
import net.mrjosh.homepi.activities.BaseActivity
import net.mrjosh.homepi.fragments.auth.ServerFragment

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