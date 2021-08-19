package com.example.myfirstapp.extentions

import android.animation.Animator
import android.animation.IntEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.RelativeLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat


/**
 * Extension method to startActivity with Animation for Context.
 */
inline fun <reified T : Activity> Context.startActivityWithAnimation(enterResId: Int = 0, exitResId: Int = 0) {
    val intent = Intent(this, T::class.java)
    val bundle = ActivityOptionsCompat.makeCustomAnimation(this, enterResId, exitResId).toBundle()
    ContextCompat.startActivity(this, intent, bundle)
}

@JvmOverloads
fun toggleArrow(show: Boolean, view: View, delay: Boolean = true): Boolean {
    return if (show) {
        view.animate().setDuration((if (delay) 200 else 0).toLong()).rotation(180f)
        true
    } else {
        view.animate().setDuration((if (delay) 200 else 0).toLong()).rotation(0f)
        false
    }
}

fun View.toggleArrow(duration: Long = 200): Boolean {
    return if (rotation == 0f) {
        animate().setDuration(duration).rotation(180f)
        true
    } else {
        animate().setDuration(duration).rotation(0f)
        false
    }
}
fun View.slideUp(){
    visibility = View.VISIBLE
    val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            height.toFloat(),  // fromYDelta
            0f) // toYDelta
    animate.duration = 500
    animate.fillAfter = true
    startAnimation(animate)
}
fun View.slideUpScaleAnimation(){
    visibility = View.VISIBLE
    val animate = ScaleAnimation(
            1f,  // fromXDelta
            1f,  // toXDelta
            0f,  // fromYDelta
            1f) // toYDelta
    animate.duration = 500
    startAnimation(animate)
}
fun View.slideDown(){
    val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            0f,  // fromYDelta
            height.toFloat()) // toYDelta
    animate.duration = 500
    animate.fillAfter = true
    startAnimation(animate)
}
/**
 * gradient(200f, 0x80C24641.toInt(), 0x80FFFFFF.toInt())
 */
fun View.gradient(radius: Float, vararg colors: Int) {
    background = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors).apply { cornerRadius = radius }
}

fun View.xAnimator(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.X, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.yAnimator(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.Y, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}


fun View.zAnimator(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.Z, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.animateTranslationX(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_X, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateTranslationY(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}


fun View.animateTranslationZ(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Z, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateScaleX(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.SCALE_X, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateScaleY(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.SCALE_Y, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateAlpha(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.ALPHA, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateRotation(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.ROTATION, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateRotationX(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.ROTATION_X, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateRotationY(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.ROTATION_Y, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateX(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.X, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.animateY(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.Y, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}


fun View.animateZ(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0) {
    val animator = ObjectAnimator.ofFloat(this, View.Z, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    animator.start()
}

fun View.translationXAnimator(
    values: FloatArray,
    duration: Long = 300,
    repeatCount: Int = 0,
    repeatMode: Int = 0
): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_X, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.translationYAnimator(
    values: FloatArray,
    duration: Long = 300,
    repeatCount: Int = 0,
    repeatMode: Int = 0
): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}


fun View.translationZAnimator(
    values: FloatArray,
    duration: Long = 300,
    repeatCount: Int = 0,
    repeatMode: Int = 0
): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Z, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.scaleXAnimator(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.SCALE_X, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.scaleYAnimator(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.SCALE_Y, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.alphaAnimator(values: FloatArray, duration: Long = 300, repeatCount: Int = 0, repeatMode: Int = 0): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.ALPHA, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.rotationAnimator(
    values: FloatArray,
    duration: Long = 300,
    repeatCount: Int = 0,
    repeatMode: Int = 0
): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.ROTATION, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.rotationXAnimator(
    values: FloatArray,
    duration: Long = 300,
    repeatCount: Int = 0,
    repeatMode: Int = 0
): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.ROTATION_X, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.rotationYAnimator(
    values: FloatArray,
    duration: Long = 300,
    repeatCount: Int = 0,
    repeatMode: Int = 0
): Animator {
    val animator = ObjectAnimator.ofFloat(this, View.ROTATION_Y, *values)
    animator.repeatCount = repeatCount
    animator.duration = duration
    if (repeatMode == ObjectAnimator.REVERSE || repeatMode == ObjectAnimator.RESTART) {
        animator.repeatMode = repeatMode
    }
    return animator
}

fun View.animateWidthAndHeight(
    targetWidth: Int,
    targetHeight: Int,
    duration: Long = 400,
    action: ((Float) -> Unit)? = null
) {
    val startHeight = height
    val evaluator = IntEvaluator()
    ValueAnimator.ofInt(width, targetWidth).apply {
        addUpdateListener {
            resize(it.animatedValue as Int, evaluator.evaluate(it.animatedFraction, startHeight, targetHeight))
            action?.invoke((it.animatedFraction))
        }
        setDuration(duration)
        start()
    }
}


fun getScreenLocation(view: View): IntArray {
    val locations = IntArray(2)
    view.getLocationOnScreen(locations)
    return locations
}
fun View.moveViewRelatively(left: Int, top: Int) {
    val location = getScreenLocation(this)
    val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    params.setMargins(left + location[0], top + location[1], 0, 0)
    val p = layoutParams
    params.width = p.width
    params.height = p.height
    layoutParams = params
}
fun View.moveView(left: Int, top: Int) {
    val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    params.setMargins(left, top, 0, 0)
    val p = layoutParams
    params.width = p.width
    params.height = p.height
    layoutParams = params
}