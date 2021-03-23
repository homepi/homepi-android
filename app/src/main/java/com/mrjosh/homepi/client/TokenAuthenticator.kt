package com.mrjosh.homepi.client

import okhttp3.Route
import okhttp3.Request
import okhttp3.Response
import android.widget.Toast
import android.app.Activity
import okhttp3.Authenticator
import com.mrjosh.homepi.models.Server
import android.accounts.AccountManager
import com.mrjosh.homepi.components.Utility
import com.mrjosh.homepi.client.responses.AuthenticationResult

class TokenAuthenticator(private val a: Activity): Authenticator {

    private var newToken: String? = null

    override fun authenticate(route: Route?, resp: Response): Request? {
        val server = a.intent.getSerializableExtra("server") as Server
        val client = Utility.getRetrofitClient(a, server)
        val response = client?.refreshToken(server.refreshedToken)!!.execute()
        when {
            response.raw().code() == 423 -> {
                Toast.makeText(a, "Your account is blocked!", Toast.LENGTH_LONG).show()
            }
            response.isSuccessful -> {
                val result: AuthenticationResult.Result = response.body()!!.result
                val accountManager: AccountManager = Utility.getAccountManager(a)
                newToken = result.token
                accountManager.setUserData(server.account, "token", newToken)
                accountManager.setUserData(server.account, "refreshed_token", result.refreshed_token)
                server.token = result.token
                server.refreshedToken = result.refreshed_token
                a.intent.putExtra("server", server)
            }
            else -> {
                a.runOnUiThread {
                    Toast.makeText(a.baseContext, "Authentication failed, Login again!", Toast.LENGTH_LONG).show()
                    Utility.removeAccountThenGoToLoginPage(a, server.account!!)
                }
                return null
            }
        }
        return resp.request().newBuilder().header("Authorization", newToken!!).build()
    }
}