package com.mrjosh.homepi.components

import android.graphics.Path
import android.graphics.RectF
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.annotation.SuppressLint
import com.elyeproj.loaderviewlibrary.LoaderImageView

class RoundLoaderImageView: LoaderImageView {
    companion object {
        var radius = 150f
    }
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val clipPath = Path()
        val rect = RectF(0F, 0F, width.toFloat(), height.toFloat())
        clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW)
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
    }
}
