package net.mrjosh.homepi.adapters

import android.view.View
import net.mrjosh.homepi.R
import android.view.ViewGroup
import android.content.Context
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import com.squareup.picasso.Picasso
import android.annotation.SuppressLint
import net.mrjosh.homepi.models.Server
import com.elyeproj.loaderviewlibrary.LoaderImageView

class ServerAdapter(context: Context?) : ArrayAdapter<Server?>(context!!, R.layout.servers_row) {

    // View lookup cache
    private class ViewHolder {
        var avatar: LoaderImageView? = null
        var name: TextView? = null
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // Get the data item for this position
        var view = convertView
        val dataModel: Server? = getItem(position)

        // Check if an existing view is being reused, otherwise inflate the view
        val viewHolder: ViewHolder // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.servers_row, parent, false)
            viewHolder.name = view.findViewById(R.id.spinnerValue)
            viewHolder.avatar = view.findViewById(R.id.avatar)
            view.tag = viewHolder
        } else {
            viewHolder = view?.tag as ViewHolder
        }

        viewHolder.name!!.text = dataModel?.name

        Picasso.get().load(dataModel?.getAvatarPath()).into(viewHolder.avatar)

        return view!!
    }
}