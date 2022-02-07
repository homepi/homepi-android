package net.mrjosh.homepi.requests

import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import okhttp3.ResponseBody
import android.content.Context
import net.mrjosh.homepi.requests.exceptions.*
import net.mrjosh.homepi.client.responses.UserResult

open class Request internal constructor(val context: Context) {
    fun checkInternetConnection(responseCall: Call<UserResult?>?, body: ResponseBody?, callback: Callback) {
        try {
            if (body != null) {
                callback.onFailure(responseCall, ErrorOnResponseException(body.string()))
            } else {
                callback.onFailure(responseCall, NullResponseException())
            }
        } catch (e: IOException) {
            e.printStackTrace()
            callback.onFailure(responseCall, e)
        }
    }
    interface Callback {
        fun onResponse(call: Call<UserResult?>?, response: Response<UserResult?>?)
        fun onFailure(call: Call<UserResult?>?, t: Throwable?)
    }
}
