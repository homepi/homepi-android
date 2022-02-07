package net.mrjosh.homepi.client.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SystemResult {

    @SerializedName("result")
    @Expose
    var result: Result? = null

    inner class Result {
        @SerializedName("avatars_pattern")
        @Expose
        val avatarsPattern: String? = null

        @SerializedName("api_base_uri")
        @Expose
        var apiBaseUri: String? = null

        @SerializedName("base_uri")
        @Expose
        var baseUri: String? = null

        @SerializedName("version")
        @Expose
        var version: String? = null
    }
}
