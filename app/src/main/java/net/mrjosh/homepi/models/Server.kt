package net.mrjosh.homepi.models

import android.accounts.Account
import java.io.Serializable

class Server(
    val name: String?,
    val baseUri: String?,
    val apiBaseUri: String?,
    val avatarsBaseUri: String?
): Serializable {

    @Transient
    var account: Account? = null
    var avatar: String? = null
    var token: String? = null
    var refreshedToken: String? = null

    val apiUri: String
        get() = baseUri + apiBaseUri

    fun getAvatarPath() : String {
        val avatarsUri = baseUri + avatarsBaseUri
        return avatarsUri.replace("{avatar_name}", this.avatar!!)
    }

    fun getAvatar(avatar: String) : String {
        val avatarsUri = baseUri + avatarsBaseUri
        return avatarsUri.replace("{avatar_name}", avatar)
    }

}