package net.mrjosh.homepi.services

import android.os.Bundle
import android.content.Intent
import android.content.Context
import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AbstractAccountAuthenticator
import net.mrjosh.homepi.activities.auth.LoginActivity

internal class Authenticator(private val context: Context) : AbstractAccountAuthenticator(context) {

    override fun addAccount(
        response: AccountAuthenticatorResponse,
        accountType: String,
        authTokenType: String,
        requiredFeatures: Array<String>,
        options: Bundle
    ): Bundle {
        val bundle = Bundle()
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra(AuthenticatorService.ARG_ACCOUNT_TYPE, accountType)
        intent.putExtra(AuthenticatorService.ARG_AUTH_TYPE, authTokenType)
        intent.putExtra(AuthenticatorService.IS_ADDING_ACCOUNT, true)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    override fun confirmCredentials(response: AccountAuthenticatorResponse, account: Account, options: Bundle): Bundle? {
        return null
    }

    override fun editProperties(response: AccountAuthenticatorResponse, accountType: String): Bundle {
        throw UnsupportedOperationException()
    }

    override fun getAuthToken(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        loginOptions: Bundle
    ): Bundle? {
        return null
    }

    override fun getAuthTokenLabel(authTokenType: String): String? {
        return null
    }

    override fun hasFeatures(response: AccountAuthenticatorResponse, account: Account, features: Array<String>): Bundle {
        val result = Bundle()
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)
        return result
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        loginOptions: Bundle
    ): Bundle? {
        return null
    }

}