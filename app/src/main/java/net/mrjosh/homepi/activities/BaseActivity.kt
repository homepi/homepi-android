package net.mrjosh.homepi.activities

import java.util.*
import android.os.Bundle
import net.mrjosh.homepi.R
import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import net.mrjosh.homepi.models.Server
import android.content.res.Configuration
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import net.mrjosh.homepi.services.AuthenticatorService

@SuppressLint("Registered")
open class BaseActivity: AppCompatActivity() {

    private var currentTheme: String = "dark"
    private val prefsFileName = "net.mrjosh.homepi.prefs"
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = this.getSharedPreferences(prefsFileName, 0)
        val theme = prefs?.getString("current_theme", "dark")
        val config: Configuration = resources.configuration
        config.setLocale(Locale("en_US"))
        baseContext.createConfigurationContext(config)
        updateCurrentTheme(theme!!)
    }

    private fun updateCurrentTheme(currentTheme: String) {
        this.currentTheme = currentTheme
        prefs!!.edit().putString("current_theme", currentTheme).apply()
        setCurrentTheme()
    }

    private fun setCurrentTheme() {
        if (currentTheme == "dark") {
            setTheme(R.style.AppTheme_Dark)
        } else {
            setTheme(R.style.AppTheme_Default)
        }
    }

    override fun onResume() {
        super.onResume()
        val theme = prefs?.getString("current_theme", "dark")
        if (currentTheme != theme) {
            recreate()
        }
    }

    fun getAccounts(): Array<Account> {
        val accountManager: AccountManager = getAccountManager()
        return accountManager.getAccountsByType(AuthenticatorService.ACCOUNT_TYPE)
    }

    private fun getAccountManager(): AccountManager {
        return AccountManager.get(baseContext)
    }

    fun getServerViaAccount(account: Account?): Server? {
        val accountManager: AccountManager = getAccountManager()
        val name = account?.name
        val baseUri = accountManager.getUserData(account, "base_uri")
        val apiBaseUri = accountManager.getUserData(account, "api_base_uri")
        val avatarsBaseUri = accountManager.getUserData(account, "avatars_base_uri")
        val avatar = accountManager.getUserData(account, "avatar")
        val token = accountManager.getUserData(account, "token")
        val refreshedToken = accountManager.getUserData(account, "refreshed_token")
        val server = Server(name!!, baseUri, apiBaseUri, avatarsBaseUri)
        server.token = token
        server.refreshedToken = refreshedToken
        server.avatar = avatar
        server.account = account
        return server
    }
}