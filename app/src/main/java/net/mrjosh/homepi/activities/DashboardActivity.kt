package net.mrjosh.homepi.activities

import android.os.Bundle
import android.view.View
import net.mrjosh.homepi.R
import android.view.MenuItem
import android.content.Intent
import android.accounts.Account
import com.squareup.picasso.Picasso
import net.mrjosh.homepi.models.Server
import android.accounts.AccountManager
import androidx.core.view.GravityCompat
import androidx.appcompat.widget.Toolbar
import net.mrjosh.homepi.components.Utility
import net.mrjosh.homepi.fragments.dashboard.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatImageButton
import net.mrjosh.homepi.components.RoundLoaderImageView
import com.google.android.material.navigation.NavigationView
import net.mrjosh.homepi.activities.auth.SelectServerActivity

class DashboardActivity: BaseActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    var server: Server? = null
    private var drawer: DrawerLayout? = null
    private var navigationView: NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        val logoutBtn: AppCompatImageButton = findViewById(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            val intent = Intent(this@DashboardActivity, SelectServerActivity::class.java)
            intent.putExtra("server", server)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        server = intent.getSerializableExtra("server") as Server
        val account: Account? = intent.getParcelableExtra("account")
        server?.account = account

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer?.addDrawerListener(toggle)
        toggle.syncState()

        navigationView = findViewById(R.id.nav_view)
        navigationView?.setNavigationItemSelectedListener(this)

        val headerLayout: View = navigationView!!.getHeaderView(0)
        switchFragment(R.id.nav_dashboard)

        val userAvatar =
            headerLayout.findViewById<RoundLoaderImageView>(R.id.userAvatar)

        val userFullName =
            headerLayout.findViewById<AppCompatTextView>(R.id.userFullName)

        val userNameAndServer =
            headerLayout.findViewById<AppCompatTextView>(R.id.userNameAndServer)

        Picasso.get().load(server?.getAvatarPath()).into(userAvatar)

        val accountManager: AccountManager = Utility.getAccountManager(this)
        val fullname = accountManager.getUserData(account, "fullname")

        userFullName.text = fullname
        userNameAndServer.text = account?.name
    }

    override fun onResume() {
        super.onResume()
        if (navigationView?.checkedItem != null) {
            switchFragment(navigationView?.checkedItem?.itemId!!)
        }
    }

    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        switchFragment(item.itemId)
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun switchFragment(id: Int) {

        val item: MenuItem = navigationView!!.menu.findItem(id)
        title = item.title

        when (id) {

            R.id.nav_dashboard -> {

                if (supportFragmentManager.findFragmentByTag("dashboard") != null) {
                    supportFragmentManager.beginTransaction()
                        .show(supportFragmentManager.findFragmentByTag("dashboard")!!)
                        .commit()
                } else {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.content, DashboardFragment(), "dashboard")
                        .commit()
                }

                if (supportFragmentManager.findFragmentByTag("logs") != null) {
                    supportFragmentManager.beginTransaction()
                        .hide(supportFragmentManager.findFragmentByTag("logs")!!)
                        .commit()
                }

                if (supportFragmentManager.findFragmentByTag("settings") != null) {
                    supportFragmentManager.beginTransaction()
                        .hide(supportFragmentManager.findFragmentByTag("settings")!!)
                        .commit()
                }

            }

            R.id.nav_logs -> {

                if (supportFragmentManager.findFragmentByTag("logs") != null) {
                    supportFragmentManager.beginTransaction()
                        .show(supportFragmentManager.findFragmentByTag("logs")!!)
                        .commit()
                } else {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.content, LogsFragment(), "logs")
                        .commit()
                }

                if (supportFragmentManager.findFragmentByTag("dashboard") != null) {
                    supportFragmentManager.beginTransaction()
                        .hide(supportFragmentManager.findFragmentByTag("dashboard")!!)
                        .commit()
                }

                if (supportFragmentManager.findFragmentByTag("settings") != null) {
                    supportFragmentManager.beginTransaction()
                        .hide(supportFragmentManager.findFragmentByTag("settings")!!)
                        .commit()
                }

            }

            R.id.nav_settings -> {

                if (supportFragmentManager.findFragmentByTag("settings") != null) {
                    supportFragmentManager.beginTransaction()
                        .show(supportFragmentManager.findFragmentByTag("settings")!!)
                        .commit()
                } else {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.content, SettingsFragment(), "settings")
                        .commit()
                }

                if (supportFragmentManager.findFragmentByTag("logs") != null) {
                    supportFragmentManager.beginTransaction()
                        .hide(supportFragmentManager.findFragmentByTag("logs")!!)
                        .commit()
                }

                if (supportFragmentManager.findFragmentByTag("dashboard") != null) {
                    supportFragmentManager.beginTransaction()
                        .hide(supportFragmentManager.findFragmentByTag("dashboard")!!)
                        .commit()
                }

            }
        }
    }

}