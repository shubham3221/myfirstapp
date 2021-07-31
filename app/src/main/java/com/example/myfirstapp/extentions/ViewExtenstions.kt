package com.example.myfirstapp.extentions

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.SystemClock
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.*
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import kotlin.math.max
import kotlin.math.min
import com.google.android.material.tabs.TabLayout
import java.lang.reflect.Field

//toolbar
fun Toolbar.changeOverflowMenuIconColor(@ColorInt color: Int) {
    try {
        val drawable = this.overflowIcon
        drawable?.apply {
            mutate()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
            } else {
                setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}
fun getDensityDpi(): Int {
    return Resources.getSystem().displayMetrics.densityDpi
}

fun getDensity(): Float {
    return Resources.getSystem().displayMetrics.density
}
fun Context.getScaledDrawable(@DrawableRes resourceId: Int, scaleInDp: Int): Drawable {

    fun roundToInt(v: Int): Int {
        return (v + 0.5).toInt()
    }


    fun dpToPx(dp: Int): Int {
        val scale = getDensity()
        return (dp * scale + 0.5f).toInt()
    }

    val options = BitmapFactory.Options()
    val metrics = resources.displayMetrics
    options.inScreenDensity = metrics.densityDpi
    options.inTargetDensity = metrics.densityDpi
    options.inDensity = DisplayMetrics.DENSITY_DEFAULT
    val px = roundToInt(dpToPx(scaleInDp))
    val bitmap = BitmapFactory.decodeResource(this.resources, resourceId, options)
    val drawable = BitmapDrawable(this.resources, Bitmap.createScaledBitmap(bitmap, px, px, true))

    bitmap.recycle()
    return drawable
}
fun View.aspect(ratio: Float = 3 / 4f) =
    post {
        val params = layoutParams
        params.height = (width / ratio).toInt()
        layoutParams = params
    }

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}
fun collapseLayout(linearLayout: LinearLayout, imageView: ImageView, dropUPIMG: Int, dropDOWNIMG: Int) {
    var firstClick = false

    imageView.setOnClickListener {
        if (firstClick) {

            TransitionManager.beginDelayedTransition(linearLayout)
            linearLayout.visibility = View.GONE
            imageView.setImageResource(dropDOWNIMG)

            firstClick = false

        } else {
            TransitionManager.beginDelayedTransition(linearLayout)
            linearLayout.visibility = View.VISIBLE
            imageView.setImageResource(dropUPIMG)

            firstClick = true

        }
    }
}

fun View.setHeight(newValue: Int) {
    val params = layoutParams
    params?.let {
        params.height = newValue
        layoutParams = params
    }
}

fun View.setWidth(newValue: Int) {
    val params = layoutParams
    params?.let {
        params.width = newValue
        layoutParams = params
    }
}


fun View.resize(width: Int, height: Int) {
    val params = layoutParams
    params?.let {
        params.width = width
        params.height = height
        layoutParams = params
    }
}

fun View.toggleVisibilityInvisibleToVisible(): View {
    visibility = if (visibility == View.VISIBLE) {
        View.INVISIBLE
    } else {
        View.VISIBLE
    }
    return this
}

/**
 * INVISIBLE TO GONE AND OTHERWISE
 */
fun View.toggleVisibilityGoneToVisible(): View {
    visibility = if (visibility == View.VISIBLE) {
        View.GONE
    } else {
        View.VISIBLE
    }
    return this
}


@UiThread
fun View.fadeIn(duration: Long) {
    this.clearAnimation()
    val anim = AlphaAnimation(this.alpha, 1.0f)
    anim.duration = duration
    this.startAnimation(anim)
}

@UiThread
fun View.fadeOut(duration: Long) {
    this.clearAnimation()
    val anim = AlphaAnimation(this.alpha, 0.0f)
    anim.duration = duration
    this.startAnimation(anim)
}
val View.isVisibile: Boolean
    get() {
        return this.visibility == View.VISIBLE
    }

val View.isGone: Boolean
    get() {
        return this.visibility == View.GONE
    }

val View.isInvisible: Boolean
    get() {
        return this.visibility == View.INVISIBLE
    }


fun View.hideIme() {
    val imm = context.getSystemService<InputMethodManager>()
    imm?.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * Get bitmap representation of view
 */
fun View.asBitmap(): Bitmap {
    val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    layout(left, top, right, bottom)
    draw(c)
    return b
}


/**
 * View artificial attribute that sets compound left drawable
 */
var TextView.drawableLeft: Int
    get() = throw IllegalAccessException("Property drawableLeft only as setter")
    set(value) {
        val drawables = compoundDrawables
        setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, value), drawables[1], drawables[2], drawables[3])
    }

/**
 * View artificial attribute that sets compound right drawable
 */
var TextView.drawableRight: Int
    get() = throw IllegalAccessException("Property drawableRight only as setter")
    set(value) {
        val drawables = compoundDrawables
        setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], ContextCompat.getDrawable(context, value), drawables[3])
    }

/**
 * View artificial attribute that sets compound top drawable
 */
var TextView.drawableTop: Int
    get() = throw IllegalAccessException("Property drawableTop only as setter")
    set(value) {
        val drawables = compoundDrawables
        setCompoundDrawablesWithIntrinsicBounds(drawables[0], ContextCompat.getDrawable(context, value), drawables[2], drawables[3])
    }

/**
 * View artificial attribute that sets compound bottom drawable
 */
var TextView.drawableBottom: Int
    get() = throw IllegalAccessException("Property drawableBottom only as setter")
    set(value) {
        val drawables = compoundDrawables
        setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawables[2], ContextCompat.getDrawable(context, value))
    }


/**
 * Convert this Drawable to Bitmap representation. Should take care of every Drawable type
 */
fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return bitmap
    }

    val bitmap = if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    } else {
        Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    }

    Canvas(bitmap).apply {
        setBounds(0, 0, width, height)
        draw(this)
    }
    return bitmap
}




fun View.fakeTouch() {
    val downTime = SystemClock.uptimeMillis()
    val eventTime = SystemClock.uptimeMillis() + 100
    val x = 0.0f
    val y = 0.0f
    val metaState = 0
    val motionEvent = MotionEvent.obtain(
        downTime,
        eventTime,
        MotionEvent.ACTION_UP,
        x,
        y,
        metaState
    )
    dispatchTouchEvent(motionEvent)
    motionEvent.recycle()
}



data class ViewDimensions(
    val left: Int = 0,
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0,
    val start: Int = 0,
    val end: Int = 0
)

fun View.isRtl() = layoutDirection == View.LAYOUT_DIRECTION_RTL




/**
 * Animates the view to rotate, can be refactored for more abstraction if needed
 *
 * @param rotation Value of rotation to be applied to view
 * @param duration Duration in millis of the rotation animation
 */
fun View.rotateAnimation(rotation: Float, duration: Long) {
    val interpolator = OvershootInterpolator()
    isActivated = if (!isActivated) {
        ViewCompat.animate(this).rotation(rotation).withLayer().setDuration(duration).setInterpolator(interpolator).start()
        !isActivated
    } else {
        ViewCompat.animate(this).rotation(0f).withLayer().setDuration(duration).setInterpolator(interpolator).start()
        !isActivated
    }
}

fun View.blink(duration: Long = 300L) {
    val anim = AlphaAnimation(0.3f, 1.0f)
    anim.duration = duration
    startAnimation(anim)
}


inline fun TabLayout.doOnTabReselected(crossinline action: (TabLayout.Tab) -> Unit) {
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            action(tab)
        }
    })
}

inline fun TabLayout.doOnTabSelected(crossinline action: (TabLayout.Tab) -> Unit) {
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            action(tab)

        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
        }
    })
}


inline fun TabLayout.doOnTabUnSelected(crossinline action: (TabLayout.Tab) -> Unit) {
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            action(tab)
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
        }
    })
}


fun Toolbar.setTitleOnClickListener(onClickListener: View.OnClickListener) {
    var titleView = toolbarTitleField.get(this) as View?
    if (titleView == null) {
        val title = title
        this.title = " " // Force Toolbar to create mTitleTextView
        this.title = title
        titleView = toolbarTitleField.get(this) as View
    }
    titleView.setOnClickListener(onClickListener)
}

fun Toolbar.setSubtitleOnClickListener(onClickListener: View.OnClickListener) {
    var subtitleView = toolbarSubtitleField.get(this) as View?
    if (subtitleView == null) {
        val subtitle = subtitle
        this.subtitle = " " // Force Toolbar to create mSubtitleTextView
        this.subtitle = subtitle
        subtitleView = toolbarSubtitleField.get(this) as View
    }
    subtitleView.setOnClickListener(onClickListener)
}

inline fun <reified T : CoordinatorLayout.Behavior<*>> View.getLayoutBehavior(): T {
    val layoutParams = layoutParams as CoordinatorLayout.LayoutParams
    return layoutParams.behavior as T
}

private val toolbarTitleField: Field by lazy(LazyThreadSafetyMode.NONE) {
    Toolbar::class.java.getDeclaredField("mTitleTextView").apply { isAccessible = true }
}

private val toolbarSubtitleField: Field by lazy(LazyThreadSafetyMode.NONE) {
    Toolbar::class.java.getDeclaredField("mSubtitleTextView").apply { isAccessible = true }
}



fun TabLayout.addMarginInTabLayout(color: Int, width: Int, height: Int, paddingFromDivider: Int) {
    val linearLayout = getChildAt(0) as LinearLayout
    linearLayout.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
    val drawable = GradientDrawable()
    drawable.setColor(color)
    drawable.setSize(width, height)
    linearLayout.dividerPadding = paddingFromDivider
    linearLayout.dividerDrawable = drawable
}


fun SeekBar.updateGestureExclusion() {
    val gestureExclusionRects = mutableListOf<Rect>()

    // Skip this call if we're not running on Android 10+
    if (Build.VERSION.SDK_INT < 29) return

    // First, lets clear out any existing rectangles
    gestureExclusionRects.clear()

    // Now lets work out which areas should be excluded. For a SeekBar this will
    // be the bounds of the thumb drawable.

    thumb?.also { t ->
        gestureExclusionRects += t.copyBounds()
    }

    // If we had other elements in this view near the edges, we could exclude them
    // here too, by adding their bounds to the list

    // Finally pass our updated list of rectangles to the system
    systemGestureExclusionRects = gestureExclusionRects
}



/**
 * Declare a variable
private var gestureDetector: ScaleGestureDetector? = null
override fun onTouchEvent(event: MotionEvent?): Boolean {
gestureDetector?.onTouchEvent(event)
return true
}
 * @receiver View
 * @param scaleFactor Float
 * @return ScaleGestureDetector
 */
fun View.pinchToZoom(scaleFactor: Float = 1.0f) = ScaleGestureDetector(context, ScaleListener(scaleFactor, this))
class ScaleListener(private var mScaleFactor: Float, private val view: View) : ScaleGestureDetector.SimpleOnScaleGestureListener() {

    override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
        mScaleFactor *= scaleGestureDetector.scaleFactor
        mScaleFactor = max(0.1f, min(mScaleFactor, 10.0f))
        view.scaleX = mScaleFactor
        view.scaleY = mScaleFactor
        return true
    }
}

fun View?.removeSelf() {
    this ?: return
    val parentView = parent as? ViewGroup ?: return
    parentView.removeView(this)
}

/** Pad this view with the insets provided by the device cutout (i.e. notch) */
@RequiresApi(Build.VERSION_CODES.P)
fun View.padWithDisplayCutout() {

    /** Helper method that applies padding from cutout's safe insets */
    fun doPadding(cutout: DisplayCutout) = setPadding(
        cutout.safeInsetLeft,
        cutout.safeInsetTop,
        cutout.safeInsetRight,
        cutout.safeInsetBottom)

    // Apply padding using the display cutout designated "safe area"
    rootWindowInsets?.displayCutout?.let { doPadding(it) }

    // Set a listener for window insets since view.rootWindowInsets may not be ready yet
    setOnApplyWindowInsetsListener { _, insets ->
        insets.displayCutout?.let { doPadding(it) }
        insets
    }
}