package com.example.myfirstapp.googleMaps.helper

import android.R
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory


object BitmapHelper {
    fun vectorToBitmap(
        context: Context,
        @DrawableRes id: Int,
        @ColorInt color: Int?,
    ): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(context.resources, id, null)
        if (vectorDrawable == null) {
            Log.e("//", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        if (color!=null){
            DrawableCompat.setTint(vectorDrawable, color)
        }
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int,
    ): BitmapDescriptor? {
        val background = ContextCompat.getDrawable(context, R.drawable.alert_dark_frame)
        background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        vectorDrawable!!.setBounds(40,
            20,
            vectorDrawable.intrinsicWidth + 40,
            vectorDrawable.intrinsicHeight + 20)
        val bitmap = Bitmap.createBitmap(background.intrinsicWidth,
            background.intrinsicHeight,
            Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        background.draw(canvas)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}