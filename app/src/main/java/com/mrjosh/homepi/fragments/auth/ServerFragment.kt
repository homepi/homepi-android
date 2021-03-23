package com.mrjosh.homepi.fragments.auth

import retrofit2.Call
import android.os.Bundle
import android.view.View
import retrofit2.Response
import android.os.Handler
import retrofit2.Callback
import com.mrjosh.homepi.R
import android.widget.Toast
import android.util.Patterns
import android.view.ViewGroup
import android.view.LayoutInflater
import android.graphics.BitmapFactory
import android.util.Log
import androidx.fragment.app.Fragment
import com.mrjosh.homepi.client.Client
import com.mrjosh.homepi.models.Server
import androidx.core.content.ContextCompat
import com.mrjosh.homepi.components.Utility
import com.mrjosh.homepi.client.responses.SystemResult
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputEditText
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton

class ServerFragment: Fragment() {

    private var serverAddress: String? = null
    private var nextStep: CircularProgressButton? = null
    private var serverAddressLayout: TextInputLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_auth_server, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nextStep = view.findViewById(R.id.nextStep)
        nextStep?.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_shape_circle)
        nextStep?.setOnClickListener {
            val serverAddressInput: TextInputEditText = view.findViewById(R.id.serverInput)
            serverAddressLayout = view.findViewById(R.id.serverLayout)
            serverAddress = serverAddressInput.text.toString()
            if (serverAddress!!.isEmpty()) {
                serverAddressLayout?.error = "Server address is required!"
            } else if (!Patterns.WEB_URL.matcher(serverAddress!!).matches()) {
                serverAddressLayout?.error = "Server address is not valid!"
            } else {
                serverAddressLayout?.error = ""
                checkServer()
            }
        }
    }

    private fun checkServer() {
        nextStep?.startAnimation()

        Log.d("GGG", serverAddress!!)

        val client: Client? = Utility.getRetrofitClient(requireActivity(), serverAddress!!)
        val request = client?.service!!
        request.enqueue(object : Callback<SystemResult?> {
            override fun onResponse(call: Call<SystemResult?>, response: Response<SystemResult?>) {
                Log.d("GGG", response.raw().code().toString())
                when {
                    response.isSuccessful -> {
                        val result: SystemResult.Result? = response.body()!!.result
                        val baseApiUri = result?.baseUri
                        val serverBaseApiUri = result?.apiBaseUri
                        val serverAvatarsUri = result?.avatarsPattern
                        val server = Server(serverAddress, baseApiUri, serverBaseApiUri, serverAvatarsUri)
                        val arguments = Bundle()
                        val fragment = UserPassFragment()
                        arguments.putSerializable("server", server)
                        fragment.arguments = arguments
                        val bmp = BitmapFactory.decodeResource(resources, R.drawable.ic_done_white_48dp)
                        nextStep?.doneLoadingAnimation(R.color.colorPrimary, bmp)
                        Handler().postDelayed({
                            nextStep?.revertAnimation()
                            parentFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    R.anim.enter_from_right,
                                    R.anim.exit_to_left,
                                    R.anim.enter_from_left,
                                    R.anim.exit_to_right
                                ).replace(R.id.steps_layout, fragment)
                                .addToBackStack(null).commit()
                        }, 1000)
                    }
                    else -> {
                        couldNotConnectError()
                    }
                }
            }
            override fun onFailure(call: Call<SystemResult?>, t: Throwable) {
                couldNotConnectError()
            }
        })
    }

    private fun couldNotConnectError() {
        nextStep?.revertAnimation()
        serverAddressLayout?.error = "Could not connect to the server!"
        Toast.makeText(activity, R.string.sureInstalledSmartHome, Toast.LENGTH_SHORT).show()
    }
}