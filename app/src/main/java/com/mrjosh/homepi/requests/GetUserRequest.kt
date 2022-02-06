package com.mrjosh.homepi.requests

import retrofit2.Call
import retrofit2.Response
import android.widget.Toast
import android.app.Activity
import android.util.Log
import java.net.ConnectException
import java.net.UnknownHostException
import com.mrjosh.homepi.models.Server
import java.net.UnknownServiceException
import com.mrjosh.homepi.components.Utility
import com.mrjosh.homepi.client.responses.UserResult

class GetUserRequest constructor(private val activity: Activity) : Request(activity) {

    private var server: Server? = null
    var callback: Callback? = null

    fun setOnSendListener(c: Callback): GetUserRequest {
        callback = c
        return this
    }

    fun setServer(server: Server?): GetUserRequest {
        this.server = server
        return this
    }

    fun execute() {
        val client = Utility.getRetrofitClient(activity, server)
        val request: Call<UserResult?>? = client?.getUser(server!!.token)
        request?.enqueue(object : retrofit2.Callback<UserResult?> {
            override fun onResponse(call: Call<UserResult?>, response: Response<UserResult?>) {
                when {
                    response.raw().code() == 423 -> {
                        Toast.makeText(context, "Your account is blocked!", Toast.LENGTH_LONG).show()
                    }
                    response.isSuccessful -> {
                        Utility.setUserData(context, response, server!!.account)
                        callback!!.onResponse(call, response)
                    }
                    else -> checkInternetConnection(call, response.errorBody(), callback!!)
                }
            }
            override fun onFailure(call: Call<UserResult?>, t: Throwable) {
                callback!!.onFailure(call, t)
                when (t) {
                    is UnknownHostException, is UnknownServiceException, is ConnectException -> {
                        Toast.makeText(
                            context,
                            "Connection error!. Server is not reachable.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        Utility.removeAccountThenGoToLoginPage(activity, server?.account!!)
                    }
                }
            }
        })
    }
}