package net.mrjosh.homepi.components

import java.util.*
import java.net.URI
import android.os.Build
import android.os.Handler
import retrofit2.Response
import retrofit2.Retrofit
import android.view.Gravity
import android.widget.Toast
import okhttp3.OkHttpClient
import android.app.Activity
import android.content.Intent
import android.content.Context
import android.accounts.Account
import java.net.URISyntaxException
import java.util.concurrent.TimeUnit
import net.mrjosh.homepi.models.User
import android.net.ConnectivityManager
import net.mrjosh.homepi.models.Server
import net.mrjosh.homepi.client.Client
import android.accounts.AccountManager
import android.os.Looper
import net.mrjosh.homepi.client.TokenAuthenticator
import net.mrjosh.homepi.client.responses.UserResult
import retrofit2.converter.gson.GsonConverterFactory
import net.mrjosh.homepi.activities.auth.LoginActivity
import net.mrjosh.homepi.services.AuthenticatorService

class Utility {

    companion object {

        fun getServers(c: Context?): List<Server?> {
            val servers: MutableList<Server?> = ArrayList()
            val accounts: Array<Account?> = this.getAccounts(c)
            if (accounts.isNotEmpty()) {
                for (account in accounts) {
                    servers.add(this.getServerViaAccount(c, account!!))
                }
            }
            return servers
        }

        private fun getAccounts(c: Context?): Array<Account?> {
            val accountManager: AccountManager = this.getAccountManager(c)
            return accountManager.getAccountsByType(AuthenticatorService.ACCOUNT_TYPE)
        }

        fun setUserData(c: Context?, response: Response<UserResult?>, account: Account?) {
            val accountManager: AccountManager = this.getAccountManager(c)
            val user: User? = response.body()!!.result
            accountManager.setUserData(account, "fullname", user?.fullname)
            accountManager.setUserData(account, "avatar", user?.avatar)
            accountManager.setUserData(account, "username", user?.username)
        }

        private fun removeAccount(a: Activity, account: Account) {
            val accounts: Array<Account?> = this.getAccounts(a)
            if (accounts.isNotEmpty()) {
                this.getAccountManager(a)
                    .removeAccount(account, a, null, null)
            }
        }

        fun removeAccountThenGoToLoginPage(a: Activity, account: Account) {
            this.removeAccount(a, account)
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(a, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                a.startActivity(intent)
            }, 1000)
        }

        fun isNetworkConnected(c: Context): Boolean {
            val cm = c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val status = cm.activeNetwork != null
            if (!status) {
                val toast = Toast.makeText(
                    c,
                    "Please make sure you're connected to a network!",
                    Toast.LENGTH_LONG
                )
                toast.setGravity(Gravity.TOP or Gravity.CENTER, 0, 100)
                toast.show()
            }
            return status
        }

        fun getServerViaAccount(c: Context?, account: Account): Server {
            val accountManager: AccountManager = this.getAccountManager(c)
            val name = account.name
            val baseUri = accountManager.getUserData(account, "base_uri")
            val apiBaseUri = accountManager.getUserData(account, "api_base_uri")
            val avatarsBaseUri = accountManager.getUserData(account, "avatars_base_uri")
            val avatar = accountManager.getUserData(account, "avatar")
            val token = accountManager.getUserData(account, "token")
            val refreshedToken = accountManager.getUserData(account, "refreshed_token")
            val server = Server(name, baseUri, apiBaseUri, avatarsBaseUri)
            server.token = token
            server.refreshedToken = refreshedToken
            server.avatar = avatar
            server.account = account
            return server
        }

        fun getAccountManager(c: Context?): AccountManager {
            return AccountManager.get(c)
        }

        fun getDomainName(url: String): String? {
            return try {
                val uri = URI(url)
                val domain = uri.host
                if (domain.startsWith("www.")) domain.substring(4) else domain
            } catch (e: URISyntaxException) {
                null
            }
        }

        fun getRetrofitClient(a: Activity, server: Server?): Client? {
            return this.getRetrofitClient(a, server!!.apiUri)
        }

        fun getRetrofitClient(a: Activity, serverAddress: String): Client? {
            var addr = serverAddress
            val httpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .authenticator(TokenAuthenticator(a))
            httpClient.addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("DEVICE", "Android")
                    .addHeader("TYPE", "phone")
                    .addHeader("MODEL", Build.MODEL)
                chain.proceed(request.build())
            }
            if (!addr.endsWith("/")) {
                addr = "$addr/"
            }
            val retrofit = Retrofit.Builder()
                .baseUrl(addr)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(Client::class.java)
        }
    }
}