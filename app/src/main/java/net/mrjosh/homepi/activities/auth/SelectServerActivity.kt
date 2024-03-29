package net.mrjosh.homepi.activities.auth

import retrofit2.Call
import android.os.Bundle
import android.view.View
import retrofit2.Response
import net.mrjosh.homepi.R
import android.widget.Toast
import android.widget.Button
import android.content.Intent
import android.widget.ListView
import net.mrjosh.homepi.models.Server
import net.mrjosh.homepi.requests.Request
import net.mrjosh.homepi.components.Utility
import net.mrjosh.homepi.adapters.ServerAdapter
import net.mrjosh.homepi.requests.GetUserRequest
import net.mrjosh.homepi.activities.BaseActivity
import com.github.ybq.android.spinkit.SpinKitView
import net.mrjosh.homepi.client.responses.UserResult
import net.mrjosh.homepi.activities.DashboardActivity
import com.apachat.loadingbutton.core.customViews.CircularProgressButton

class SelectServerActivity: BaseActivity(), Request.Callback {

    private var server: Server? = null
    private var spinner: SpinKitView? = null
    private var loginBtn: CircularProgressButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_server)

        val serversList: ListView = findViewById(R.id.servers_list)
        val serversAdapter = ServerAdapter(this)
        serversAdapter.addAll(Utility.getServers(this))
        serversList.adapter = serversAdapter

        val addNewAccountBtn: Button = findViewById(R.id.add_new_account)
        addNewAccountBtn.setOnClickListener {
            val intent = Intent(this@SelectServerActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        serversList.setOnItemClickListener { _, view, position, _ ->
            spinner = view.findViewById(R.id.spinner)
            spinner?.visibility = View.VISIBLE
            server = serversAdapter.getItem(position)
            GetUserRequest(this).setOnSendListener(this).setServer(server).execute()
        }
    }

    override fun onResponse(call: Call<UserResult?>?, response: Response<UserResult?>?) {
        when {
            response!!.isSuccessful -> {
                spinner?.visibility = View.GONE
                val intent = Intent(this@SelectServerActivity, DashboardActivity::class.java)
                intent.putExtra("server", server)
                intent.putExtra("account", server?.account)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            else -> {
                spinner?.visibility = View.GONE
                Toast.makeText(this, "Try again!", Toast.LENGTH_LONG).show()
                loginBtn?.revertAnimation()
            }
        }

    }

    override fun onFailure(call: Call<UserResult?>?, t: Throwable?) {
        spinner!!.visibility = View.GONE
    }

}