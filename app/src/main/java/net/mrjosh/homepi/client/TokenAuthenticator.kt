package net.mrjosh.homepi.client

import okhttp3.Route
import okhttp3.Request
import okhttp3.Response
import android.widget.Toast
import android.app.Activity
import okhttp3.Authenticator
import net.mrjosh.homepi.models.Server
import android.accounts.AccountManager
import android.util.Log
import net.mrjosh.homepi.components.Utility
import net.mrjosh.homepi.client.responses.AuthenticationResult

class TokenAuthenticator(private val a: Activity): Authenticator {

    override fun authenticate(route: Route?, resp: Response): Request? {
        val newToken: String
        val newRefreshToken: String
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
                newRefreshToken = result.refreshed_token
                accountManager.setUserData(server.account, "token", "Bearer $newToken")
                accountManager.setUserData(server.account, "refreshed_token", "Bearer $newRefreshToken")
                server.token = "Bearer $newToken"
                server.refreshedToken = "Bearer $newRefreshToken"
                a.intent.putExtra("server", server)
                Log.d("Authentication::NewToken", newToken)
                Log.d("Authentication::NewRefreshToken", newRefreshToken)
                return resp.request().newBuilder().header("Authorization", server.token!!).build()
            }
            else -> {
                a.runOnUiThread {
                    Toast.makeText(a.baseContext, "Authentication failed, Login again!", Toast.LENGTH_LONG).show()
                    Utility.removeAccountThenGoToLoginPage(a, server.account!!)
                }
            }
        }
        return null
    }
}