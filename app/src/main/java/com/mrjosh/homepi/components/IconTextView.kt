package com.mrjosh.homepi.components

import com.mrjosh.homepi.R
import android.view.Gravity
import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import androidx.appcompat.widget.AppCompatTextView

class IconTextView : AppCompatTextView {

    constructor(context: Context) : super(context) {
        createView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        createView()
    }

    private fun createView() {
        gravity = Gravity.CENTER
        typeface = ResourcesCompat.getFont(context, R.font.fontawesome)
    }
}