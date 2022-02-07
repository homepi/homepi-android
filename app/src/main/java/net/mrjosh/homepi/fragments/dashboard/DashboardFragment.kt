package net.mrjosh.homepi.fragments.dashboard

import android.app.Dialog
import java.util.*
import retrofit2.Call
import android.util.Log
import android.os.Bundle
import android.view.View
import retrofit2.Response
import retrofit2.Callback
import org.json.JSONObject
import java.io.IOException
import net.mrjosh.homepi.R
import android.widget.Toast
import android.view.ViewGroup
import android.widget.GridView
import android.widget.AdapterView
import android.view.LayoutInflater
import net.mrjosh.homepi.models.Server
import net.mrjosh.homepi.models.Accessory
import net.mrjosh.homepi.components.Utility
import net.mrjosh.homepi.fragments.BaseFragment
import androidx.appcompat.widget.AppCompatButton
import com.github.ybq.android.spinkit.SpinKitView
import net.mrjosh.homepi.adapters.AccessoryAdapter
import net.mrjosh.homepi.activities.DashboardActivity
import net.mrjosh.homepi.client.responses.AccessoriesResult
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class DashboardFragment: BaseFragment(),
    AdapterView.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    var server: Server? = null
    var spinner: SpinKitView? = null
    var accessories: MutableList<Accessory>? = null
    var accessoriesGridview: GridView? = null
    var refreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_accessories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dashActivity = requireActivity() as DashboardActivity?
        server = dashActivity?.server

        accessoriesGridview = view.findViewById(R.id.accessories_gridview)
        refreshLayout = view.findViewById(R.id.accessories_refresh_layout)
        accessories = ArrayList<Accessory>()
        refreshLayout?.setOnRefreshListener(this)

        loadAccessories()
    }

    override fun onRefresh() {
        refreshLayout?.isRefreshing = true
        loadAccessories()
    }

    private fun loadAccessories() {

        accessories?.clear()

        val request: Call<AccessoriesResult?>? = Utility.getRetrofitClient(requireActivity(), server)!!
            .getAccessories(server?.token)

        request?.enqueue(object : Callback<AccessoriesResult?> {
            override fun onResponse(call: Call<AccessoriesResult?>, response: Response<AccessoriesResult?>) {
                if (response.isSuccessful) {
                    refreshLayout?.isRefreshing = false
                    val result: List<Accessory>? = response.body()?.result
                    accessories?.addAll(result!!)
                    val adapter = AccessoryAdapter(requireContext(), accessories!!)
                    accessoriesGridview?.adapter = adapter
                    accessoriesGridview?.onItemClickListener = this@DashboardFragment
                } else {
                    try {
                        Log.d("reqErrorFromResponse", response.errorBody().toString())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    refreshLayout?.isRefreshing = false
                }
            }
            override fun onFailure(call: Call<AccessoriesResult?>, t: Throwable) {
                Log.d("reqErrorFromThrow", t.message!!)
                refreshLayout?.isRefreshing = false
            }
        })
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        val accessory: Accessory = accessories!![position]
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.run_accessory_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        dialog.findViewById<AppCompatButton>(R.id.run).setOnClickListener { dialogView ->
            dialog.dismiss()
            runAccessory(accessory, dialogView)
        }
        dialog.findViewById<AppCompatButton>(R.id.cancel).setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun runAccessory(accessory: Accessory, view: View) {
        spinner = view.findViewById(R.id.spinner)
        accessoriesGridview?.isEnabled = false
        spinner?.visibility = View.VISIBLE
        val client = Utility.getRetrofitClient(requireActivity(), server)
        val request: Call<JSONObject?>? = client?.runAccessory(server?.token, accessory.id)
        request?.enqueue(object : Callback<JSONObject?> {
            override fun onResponse(call: Call<JSONObject?>, response: Response<JSONObject?>) {
                when {
                    response.isSuccessful -> {
                        accessoriesGridview?.isEnabled = true
                        spinner?.visibility = View.GONE
                        Toast.makeText(activity, "Task ran successfully.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        try {
                            Log.d("reqErrorFromResponse", response.errorBody()!!.string())
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        errorOnAccessoryTask()
                    }
                }
            }
            override fun onFailure(call: Call<JSONObject?>, t: Throwable) {
                Log.d("reqErrorFromThrow", t.message!!)
                errorOnAccessoryTask()
            }
        })
    }

    fun errorOnAccessoryTask() {
        Toast.makeText(activity, "Something got wrong. Please try again!", Toast.LENGTH_LONG).show()
        accessoriesGridview?.isEnabled = true
        spinner?.visibility = View.GONE
    }
}