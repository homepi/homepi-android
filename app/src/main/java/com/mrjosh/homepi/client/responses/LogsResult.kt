package com.mrjosh.homepi.client.responses

import com.mrjosh.homepi.models.Log
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LogsResult {

    @SerializedName("result")
    @Expose
    val result: Result? = null

    inner class Result {
        @SerializedName("data")
        @Expose
        val data: List<Log>? = null
    }

}