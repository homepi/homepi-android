package com.mrjosh.homepi.services

import android.os.IBinder
import android.app.Service
import android.content.Intent

class AuthenticatorService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        val authenticator = Authenticator(this)
        return authenticator.iBinder
    }
    companion object {
        var ARG_AUTH_TYPE = "AUTH_TYPE"
        var ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE"
        var IS_ADDING_ACCOUNT = "IS_ADDING_ACCOUNT"
        var ACCOUNT_TYPE = "com.mrjosh.homepi.account"
    }
}