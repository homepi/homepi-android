package net.mrjosh.homepi.adapters

import android.view.View
import net.mrjosh.homepi.R
import android.view.ViewGroup
import android.content.Context
import android.widget.BaseAdapter
import android.view.LayoutInflater
import android.annotation.SuppressLint
import net.mrjosh.homepi.models.Accessory
import androidx.core.content.res.ResourcesCompat
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.AppCompatImageView

class AccessoryAdapter(private val context: Context,
                       private val accessories: List<Accessory>): BaseAdapter() {

    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View? {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val gridView = inflater.inflate(R.layout.accessory_card_view, null)
        var icon: Int = R.drawable.ic_light_on
        val accessory: Accessory = accessories[position]
        val accessoryName: AppCompatTextView = gridView.findViewById(R.id.accessory_name)
        accessoryName.text = accessory.name
        val accessoryIcon: AppCompatImageView = gridView.findViewById(R.id.accessory_icon)
        when (accessory.icon) {
            "doorbell" -> icon = R.drawable.ic_doorbell
            "lamp" -> icon = R.drawable.ic_light_on
        }
        accessoryIcon.background = ResourcesCompat.getDrawable(context.resources, icon, null)
        return gridView
    }

    override fun getCount(): Int {
        return accessories.size
    }

    override fun getItem(position: Int): Any {
        return accessories[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
}