package net.mrjosh.homepi.activities

import retrofit2.Call
import android.os.Bundle
import android.view.View
import retrofit2.Response
import android.os.Handler
import net.mrjosh.homepi.R
import android.content.Intent
import android.accounts.Account
import net.mrjosh.homepi.models.Server
import net.mrjosh.homepi.requests.Request
import net.mrjosh.homepi.requests.GetUserRequest
import androidx.appcompat.widget.AppCompatButton
import com.github.ybq.android.spinkit.SpinKitView
import net.mrjosh.homepi.client.responses.UserResult
import androidx.appcompat.widget.AppCompatImageButton
import net.mrjosh.homepi.activities.auth.LoginActivity
import net.mrjosh.homepi.activities.auth.SelectServerActivity

class LaunchActivity: BaseActivity(), Request.Callback {

    private var server: Server? = null
    private var account: Account? = null
    private var spinner: SpinKitView? = null
    private var accountsBtn: AppCompatButton? = null
    private var refreshBtn: AppCompatImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        spinner = findViewById(R.id.spinner)
        refreshBtn = findViewById(R.id.refreshBtn)
        accountsBtn = findViewById(R.id.accounts_btn)

        accountsBtn?.setOnClickListener {
            val intent = Intent(this@LaunchActivity, SelectServerActivity::class.java)
            intent.putExtra("server", server)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        refreshBtn?.setOnClickListener {
            loginToAccount()
        }

        val accounts: Array<Account> = getAccounts()

        Handler().postDelayed({
            val newIntent: Intent
            if (accounts.size == 1) {
                account = accounts[0]
                server = getServerViaAccount(account)
                intent.putExtra("server", server)
                // Refresh the account token and login
                loginToAccount()
            } else {
                newIntent = when {
                    accounts.isEmpty() -> {
                        Intent(this, LoginActivity::class.java)
                    }
                    else -> {
                        Intent(this, SelectServerActivity::class.java)
                    }
                }
                intent.putExtra("server", server)
                newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(newIntent)
            }
        }, 1000)
    }

    private fun loginToAccount() {
        refreshBtn?.visibility = View.GONE
        spinner?.visibility = View.VISIBLE
        accountsBtn?.visibility = View.GONE
        if (account != null && server != null) {
            GetUserRequest(this).setOnSendListener(this).setServer(server).execute()
        }
    }

    override fun onResponse(call: Call<UserResult?>?, response: Response<UserResult?>?) {
        when {
            response!!.isSuccessful -> {
                spinner?.visibility = View.GONE
                val intent = Intent(this@LaunchActivity, DashboardActivity::class.java)
                intent.putExtra("server", server)
                intent.putExtra("account", account)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            else -> {
                spinner?.visibility = View.GONE
                refreshBtn!!.visibility = View.VISIBLE
            }
        }
    }

    override fun onFailure(call: Call<UserResult?>?, t: Throwable?) {
        spinner?.visibility = View.GONE
        refreshBtn!!.visibility = View.VISIBLE
        accountsBtn!!.visibility = View.VISIBLE
    }

}