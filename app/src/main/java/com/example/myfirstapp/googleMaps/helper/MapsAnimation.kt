package com.example.myfirstapp.googleMaps.helper

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.animation.LinearInterpolator
import com.example.myfirstapp.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.coroutineScope

object MapsAnimation {
    private lateinit var greyPolyLine: Polyline
    private lateinit var blackPolyLine: Polyline
    private lateinit var mPolylineOptions: PolylineOptions
    private lateinit var animator: ValueAnimator
    private var repeatTime = 0

    fun addPolylineWithAnimation(mMap:GoogleMap,mutableList: List<LatLng> , repeat:Int){
        this.repeatTime = repeat
        mPolylineOptions = PolylineOptions()
        mPolylineOptions.width(10f)
        mPolylineOptions.color(Color.parseColor("#191a23"))
        mPolylineOptions.startCap(SquareCap())
        mPolylineOptions.endCap(SquareCap())
        mPolylineOptions.jointType(JointType.ROUND)

        mPolylineOptions.addAll(mutableList)

        blackPolyLine = mMap.addPolyline(mPolylineOptions)


        val greyOptions = PolylineOptions()
        greyOptions.width(10f)
        greyOptions.color(Color.GRAY)
        greyOptions.startCap(SquareCap())
        greyOptions.endCap(SquareCap())
        greyOptions.jointType(JointType.ROUND)

        greyPolyLine = mMap.addPolyline(greyOptions)

        animatePolyLine(mutableList )
    }

    private fun animatePolyLine(mutableList: List<LatLng>) {
        animator = ValueAnimator.ofInt(0, 100)
        animator.duration = 2500
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { animator ->
            val latLngList: MutableList<LatLng> = blackPolyLine.getPoints()
            val initialPointSize = latLngList.size
            val animatedValue = animator.animatedValue as Int
            val newPoints: Int = animatedValue * mutableList.size / 100
            if (initialPointSize < newPoints) {
                latLngList.addAll(mutableList.subList(initialPointSize, newPoints))
                blackPolyLine.points = latLngList
            }
        }
        animator.addListener(polyLineAnimationListener)
        animator.start()
    }

    private var polyLineAnimationListener: Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animator: Animator?) {
//            addMarker(mPolylineList.get(mPolylineList.size() - 1))
        }

        override fun onAnimationEnd(animator: Animator) {
            if (repeatTime==0) return
            val blackLatLng: MutableList<LatLng> = blackPolyLine.getPoints()
            val greyLatLng: MutableList<LatLng> = greyPolyLine.getPoints()
            greyLatLng.clear()
            greyLatLng.addAll(blackLatLng)
            blackLatLng.clear()
            blackPolyLine.points = blackLatLng
            greyPolyLine.points = greyLatLng
            blackPolyLine.zIndex = 2f
            animator.start()
            repeatTime--
        }

        override fun onAnimationCancel(animator: Animator?) {}
        override fun onAnimationRepeat(animator: Animator?) {}
    }

    fun cancelAnimation(){
        animator.cancel()
    }

    fun showCurvedPolyline(mMap: GoogleMap,p1: LatLng, p2: LatLng, k: Double = 0.5) {
        //Calculate distance and heading between two points
        val d = SphericalUtil.computeDistanceBetween(p1, p2)
        val h = SphericalUtil.computeHeading(p1, p2)

        //Midpoint position
        val p = SphericalUtil.computeOffset(p1, d * 0.5, h)

        //Apply some mathematics to calculate position of the circle center
        val x = (1 - k * k) * d * 0.5 / (2 * k)
        val r = (1 + k * k) * d * 0.5 / (2 * k)
        val c = SphericalUtil.computeOffset(p, x, h + 90.0)

        //Polyline options
        val options = PolylineOptions()

        //Calculate heading between circle center and two points
        val h1 = SphericalUtil.computeHeading(c, p1)
        val h2 = SphericalUtil.computeHeading(c, p2)

        //Calculate positions of points on circle border and add them to polyline options
        val numpoints = 100
        val step = (h2 - h1) / numpoints
        for (i in 0 until numpoints) {
            val pi = SphericalUtil.computeOffset(c, r, h1 + i * step)
            options.add(pi)
        }

        //Draw polyline
        mMap.addPolyline(options.width(10f).color(Color.RED).geodesic(false))
    }

    fun addOverlay(place: LatLng? , mMap: GoogleMap , context: Context) {
        val groundOverlay = mMap.addGroundOverlay(GroundOverlayOptions()
            .position(place, 100f)
//                .transparency(0.5f)
            .zIndex(3f)
            .image(BitmapDescriptorFactory.fromBitmap(
                MapsHelper.drawableToBitmap(
                    context.getDrawable(
                        R.drawable.map_overlay
                    )!!
                )
            )))
        startOverlayAnimation(groundOverlay!!)
    }

    private fun startOverlayAnimation(groundOverlay: GroundOverlay) {
        val animatorSet = AnimatorSet()
        val vAnimator = ValueAnimator.ofInt(0, 100)
        vAnimator.repeatCount = ValueAnimator.INFINITE
        vAnimator.repeatMode = ValueAnimator.RESTART
        vAnimator.interpolator = LinearInterpolator()
        vAnimator.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            groundOverlay.setDimensions(`val`.toFloat())
        }
        val tAnimator = ValueAnimator.ofFloat(0f, 1f)
        tAnimator.repeatCount = ValueAnimator.INFINITE
        tAnimator.repeatMode = ValueAnimator.RESTART
        tAnimator.interpolator = LinearInterpolator()
        tAnimator.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Float
            groundOverlay.transparency = `val`
        }
        animatorSet.duration = 1000
        animatorSet.playTogether(vAnimator, tAnimator)
        animatorSet.start()
    }

}