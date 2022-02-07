package net.mrjosh.homepi.fragments.dashboard

import java.util.*
import retrofit2.Call
import android.util.Log
import android.os.Bundle
import android.view.View
import retrofit2.Response
import retrofit2.Callback
import java.io.IOException
import net.mrjosh.homepi.R
import android.view.ViewGroup
import android.widget.GridView
import android.widget.AdapterView
import android.view.LayoutInflater
import net.mrjosh.homepi.models.Server
import net.mrjosh.homepi.components.Utility
import net.mrjosh.homepi.adapters.LogAdapter
import net.mrjosh.homepi.fragments.BaseFragment
import net.mrjosh.homepi.models.Log as LogModel
import net.mrjosh.homepi.client.responses.LogsResult
import net.mrjosh.homepi.activities.DashboardActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class LogsFragment: BaseFragment(), AdapterView.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    private var server: Server? = null
    private var logsGridview: GridView? = null
    private var logs: MutableList<LogModel>? = null
    private var refreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_logs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dashActivity = activity as DashboardActivity?
        server = dashActivity?.server
        refreshLayout = view.findViewById(R.id.logs_refresh_layout)
        refreshLayout?.setOnRefreshListener(this)
        logsGridview = view.findViewById(R.id.logs_gridview)
        logs = ArrayList<LogModel>()
        loadLogs()
    }

    private fun loadLogs() {

        refreshLayout?.isRefreshing = true
        logs?.clear()

        val client = Utility.getRetrofitClient(requireActivity(), server)
        val request = client?.getLogs(server?.token)

        request?.enqueue(object : Callback<LogsResult?> {
            override fun onResponse(call: Call<LogsResult?>, response: Response<LogsResult?>) {
                when {
                    response.isSuccessful -> {
                        Log.d("LOGS", response.raw().body().toString())
                        refreshLayout?.isRefreshing = false
                        val result: LogsResult.Result? = response.body()?.result
                        logs?.addAll(result?.data!!)
                        val adapter = LogAdapter(requireContext(), logs!!, server!!)
                        logsGridview?.adapter = adapter
                        logsGridview?.onItemClickListener = this@LogsFragment
                    }
                    else -> {
                        try {
                            Log.d("reqErrorFromResponse", response.errorBody().toString())
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        refreshLayout?.isRefreshing = false
                    }
                }
            }
            override fun onFailure(call: Call<LogsResult?>, t: Throwable) {
                Log.d("reqErrorFromThrow", t.message!!)
                refreshLayout?.isRefreshing = false
            }
        })
    }
    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {}
    override fun onRefresh() { loadLogs() }
}