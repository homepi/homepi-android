package net.mrjosh.homepi.fragments.auth

import retrofit2.Call
import android.util.Log
import android.os.Bundle
import android.view.View
import retrofit2.Response
import retrofit2.Callback
import net.mrjosh.homepi.R
import android.widget.Toast
import android.content.Intent
import android.view.ViewGroup
import android.accounts.Account
import android.view.LayoutInflater
import net.mrjosh.homepi.models.User
import androidx.fragment.app.Fragment
import android.graphics.BitmapFactory
import android.accounts.AccountManager
import net.mrjosh.homepi.client.Client
import net.mrjosh.homepi.models.Server
import androidx.core.content.ContextCompat
import net.mrjosh.homepi.components.Utility
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import net.mrjosh.homepi.client.responses.UserResult
import net.mrjosh.homepi.activities.DashboardActivity
import net.mrjosh.homepi.services.AuthenticatorService
import net.mrjosh.homepi.client.responses.AuthenticationResult
import com.apachat.loadingbutton.core.customViews.CircularProgressButton

class UserPassFragment: Fragment() {

    private var server: Server? = null
    private var username: String? = null
    private var password: String? = null
    private var serverAddress: AppCompatTextView? = null
    private var usernameInput: AppCompatEditText? = null
    private var passwordInput: AppCompatEditText? = null
    private var loginBtn: CircularProgressButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_auth_user_pass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.editServerBtn).setOnClickListener {
            activity?.onBackPressed()
        }
        server = arguments?.getSerializable("server") as Server?
        serverAddress = view.findViewById(R.id.serverAddress)
        usernameInput = view.findViewById(R.id.usernameInput)
        passwordInput = view.findViewById(R.id.passwordInput)
        loginBtn = view.findViewById(R.id.loginBtn)
        serverAddress?.text = server?.baseUri
        loginBtn?.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_shape_circle)
        loginBtn?.setOnClickListener {
            username = usernameInput?.text.toString()
            password = passwordInput?.text.toString()
            when {
                username!!.isEmpty() -> {
                    usernameInput?.error = "Username is required!"
                }
                password!!.isEmpty() -> {
                    passwordInput?.error = "Password is not valid!"
                }
                else -> {
                    doLogin()
                }
            }
        }
    }

    private fun doLogin() {
        if (Utility.isNetworkConnected(requireContext())) {
            loginBtn?.startAnimation()
            val client: Client? = Utility.getRetrofitClient(requireActivity(), server)
            val request = client!!.authenticate(username, password)
            request!!.enqueue(object : Callback<AuthenticationResult?> {
                override fun onResponse(call: Call<AuthenticationResult?>, response: Response<AuthenticationResult?>) {
                    when {
                        response.isSuccessful -> {
                            val tokenResult: AuthenticationResult.Result = response.body()!!.result
                            val userRequest = client.getUser("Bearer " + tokenResult.token)
                            userRequest!!.enqueue(object : Callback<UserResult?> {
                                override fun onResponse(call: Call<UserResult?>, userResponse: Response<UserResult?>) {
                                    when {
                                        userResponse.isSuccessful -> {
                                            val user: User? = userResponse.body()!!.result
                                            val accountName: String? = user!!.username + "@" + Utility.getDomainName(server?.baseUri!!)
                                            val accountType: String = AuthenticatorService.ACCOUNT_TYPE
                                            val account = Account(accountName, accountType)
                                            val accountManager: AccountManager = Utility.getAccountManager(requireContext())
                                            val userData = Bundle()

                                            userData.putString("avatar", user.avatar)
                                            userData.putString("username", user.username)
                                            userData.putString("fullname", user.fullname)
                                            userData.putString("base_uri", server?.baseUri)
                                            userData.putString("avatars_base_uri", server?.avatarsBaseUri)
                                            userData.putString("api_base_uri", server?.apiBaseUri)
                                            userData.putString("token", "Bearer " + tokenResult.token)
                                            userData.putString("refreshed_token", "Bearer " + tokenResult.refreshed_token)

                                            Log.d("Account-Details", userData.toString())
                                            accountManager.addAccountExplicitly(account, null, userData)

                                            val intent = Intent(activity, DashboardActivity::class.java)

                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            val server: Server? = Utility.getServerViaAccount(activity, account)

                                            intent.putExtra("server", server)
                                            intent.putExtra("account", account)

                                            val bmp = BitmapFactory.decodeResource(resources, R.drawable.ic_done_white_48dp)

                                            loginBtn?.doneLoadingAnimation(R.color.colorPrimary, bmp)

                                            loginBtn?.postDelayed({
                                                startActivity(intent)
                                                loginBtn?.revertAnimation()
                                            }, 1000)
                                        }
                                        else -> {
                                            Log.d("Login-failed-response", response.toString())
                                            Toast.makeText(activity, R.string.authenticationFailed, Toast.LENGTH_SHORT).show()
                                            loginBtn?.revertAnimation()
                                        }
                                    }
                                }
                                override fun onFailure(call: Call<UserResult?>, t: Throwable) {
                                    loginBtn?.revertAnimation()
                                }
                            })
                        }
                        else -> {
                            Log.d("Login-failed-response", response.toString())
                            val bmp = BitmapFactory.decodeResource(resources, R.drawable.ic_dialog_close_dark)
                            loginBtn?.doneLoadingAnimation(android.R.color.holo_red_dark, bmp)
                            when (response.code()) {
                                401, 420 -> {
                                    passwordInput!!.setText("")
                                    passwordInput!!.error = "Unauthorized!"
                                }
                                404 -> {
                                    passwordInput!!.setText("")
                                    usernameInput!!.error = "User does not exists!"
                                }
                                else -> {
                                    Toast.makeText(activity, R.string.notReachableServer, Toast.LENGTH_SHORT).show()
                                }
                            }
                            loginBtn?.postDelayed({
                                loginBtn?.revertAnimation()
                            }, 1000)
                        }
                    }
                }
                override fun onFailure(call: Call<AuthenticationResult?>, t: Throwable) {
                    Log.d("Login-failed-response", t.toString())
                    Toast.makeText(activity, R.string.authenticationFailed, Toast.LENGTH_SHORT).show()
                    loginBtn?.revertAnimation()
                }
            })
        } else {
            loginBtn?.revertAnimation()
        }
    }
}
