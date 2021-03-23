package com.mrjosh.homepi.client.responses

import com.google.gson.annotations.Expose
import com.mrjosh.homepi.models.Accessory
import com.google.gson.annotations.SerializedName

class AccessoriesResult {

    @SerializedName("result")
    @Expose
    val result: Result? = null

    inner class Result {
        @SerializedName("data")
        @Expose
        val data: List<Accessory>? = null
    }

}