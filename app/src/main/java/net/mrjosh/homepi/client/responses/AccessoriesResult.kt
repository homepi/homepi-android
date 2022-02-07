package net.mrjosh.homepi.client.responses

import com.google.gson.annotations.Expose
import net.mrjosh.homepi.models.Accessory
import com.google.gson.annotations.SerializedName

class AccessoriesResult {
    @SerializedName("result")
    @Expose
    val result: List<Accessory>? = null
}