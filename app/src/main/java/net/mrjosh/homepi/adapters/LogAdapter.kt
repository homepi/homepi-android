package net.mrjosh.homepi.adapters

import java.util.Locale
import android.view.View
import net.mrjosh.homepi.R
import android.view.ViewGroup
import android.content.Context
import android.widget.BaseAdapter
import java.text.SimpleDateFormat
import android.view.LayoutInflater
import net.mrjosh.homepi.models.Log
import com.squareup.picasso.Picasso
import android.annotation.SuppressLint
import net.mrjosh.homepi.models.Server
import androidx.appcompat.widget.AppCompatTextView
import net.mrjosh.homepi.components.RoundLoaderImageView

class LogAdapter(private val context: Context,
                 private val logs: List<Log>,
                 private val server: Server) : BaseAdapter() {

    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View? {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val gridView = inflater.inflate(R.layout.log_card_view, null)
        val logEvent = gridView.findViewById<AppCompatTextView>(R.id.log_event)
        val logCreatedAt = gridView.findViewById<AppCompatTextView>(R.id.log_created_at)
        val avatar: RoundLoaderImageView = gridView.findViewById(R.id.avatar)
        val log: Log = logs[position]
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale("en", "IR"))
        val outputFormat = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale("en", "IR"))
        Picasso.get().load(server.getAvatar(log.user.avatar)).into(avatar)
        try {
            val date = format.parse(log.created_at)
            val outputDate = outputFormat.format(date!!)
            logCreatedAt.text = outputDate
        } catch (e: Exception) {
            e.printStackTrace()
        }
        logEvent.text = log.accessory.description
        return gridView
    }

    override fun getCount(): Int {
        return logs.size
    }

    override fun getItem(position: Int): Any {
        return logs[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
}