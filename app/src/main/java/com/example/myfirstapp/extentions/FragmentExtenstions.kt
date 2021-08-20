package com.example.myfirstapp.extentions

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.Nullable
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.coroutineScope


/**
 * Set arguments to fragment and return current instance
 */
inline fun <reified T : Fragment> T.withArguments(args: Bundle): T {
    this.arguments = args
    return this
}

fun Fragment.drawable(@DrawableRes id: Int): Drawable? = ContextCompat.getDrawable(requireContext(), id)

fun FragmentActivity.popFragment() {
    val fm = supportFragmentManager
    if (fm.backStackEntryCount == 0) return
    fm.popBackStack()
}
fun FragmentActivity.popFragment(name: String, flags: Int) {
    val fm = supportFragmentManager
    if (fm.backStackEntryCount == 0) return
    fm.popBackStack(name, flags)
}

fun FragmentActivity.popFragment(id: Int, flags: Int) {
    val fm = supportFragmentManager
    if (fm.backStackEntryCount == 0) return
    fm.popBackStack(id, flags)
}

fun AppCompatActivity.getCurrentActiveFragment(@IdRes frameId: Int): Fragment? {
    return supportFragmentManager.findFragmentById(frameId)
}

fun AppCompatActivity.clearAllFragments() {
    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}

fun Fragment.isFragmentPresent(tag: String): Fragment? {
    return parentFragmentManager.findFragmentByTag(tag)
}

fun Fragment.isFragmentPresent(id: Int): Fragment? {
    return parentFragmentManager.findFragmentById(id)
}

/**
 * Call's Parent activity's `setSupportActionBar` from Fragment
 * @receiver Fragment
 * @param[toolbar]: Toolbar to set support actionbar
 */
fun Fragment.setSupportActionbar(toolbar: Toolbar) {
    val appcompatActivity = this.activity as AppCompatActivity?
    appcompatActivity?.setSupportActionBar(toolbar)
}

fun Context.isFragmentWithTagVisible(tag: String): Boolean {
    (this as AppCompatActivity)
    val presentFragment = this.supportFragmentManager.findFragmentByTag(tag)?.isVisible

    return if (presentFragment != null) {
        this.supportFragmentManager.findFragmentByTag(tag) != null && presentFragment
    } else {
        false
    }
}

fun AppCompatActivity.addFragment(fragment: Fragment, @Nullable tag: String, @IdRes layoutId: Int) {
    supportFragmentManager
        .beginTransaction()
        .add(layoutId, fragment, tag)
        .commit()
}

fun Fragment.addFragment(fragment: Fragment, @Nullable tag: String, @IdRes layoutId: Int) {
    parentFragmentManager
        .beginTransaction()
        .add(layoutId, fragment, tag)
        .commit()
}

fun Fragment.addFragment(@Nullable title: String?, fragment: Fragment, @Nullable tag: String, @IdRes layoutId: Int) {
    val activity = this.requireContext() as AppCompatActivity

    activity.supportFragmentManager
        .beginTransaction()
        .add(layoutId, fragment, tag)
        .commit()
    if (title != null) {
        if (activity.supportActionBar != null) {
            activity.supportActionBar?.title = title
        }
    }
}


fun AppCompatActivity.getFragmentWithTag(tag: String): Fragment? {
    return this.supportFragmentManager.findFragmentByTag(tag)
}

fun Context.replaceFragment(@StringRes title: Int, fragment: Fragment, @Nullable tag: String, @IdRes layoutId: Int) {
    (this as AppCompatActivity)
        .supportFragmentManager
        .beginTransaction()
        .replace(layoutId, fragment, tag)
        .commit()
    if (this.supportActionBar != null) {
        this.supportActionBar?.setTitle(title)
    }
}

fun Context.replaceFragment(@Nullable title: String?, fragment: Fragment, @Nullable tag: String, @IdRes layoutId: Int) {
    (this as AppCompatActivity)
        .supportFragmentManager
        .beginTransaction()
        .replace(layoutId, fragment, tag)
        .commit()
    if (title != null) {
        if (this.supportActionBar != null) {
            this.supportActionBar?.title = title
        }
    }
}

fun Context.addFragment(@Nullable title: String?, fragment: Fragment, @Nullable tag: String, @IdRes layoutId: Int) {
    (this as AppCompatActivity)
        .supportFragmentManager
        .beginTransaction()
        .add(layoutId, fragment, tag)
        .commit()
    if (title != null) {
        if (this.supportActionBar != null) {
            this.supportActionBar?.title = title
        }
    }
}

fun AppCompatActivity.removeFragmentBackstack(fragment: Fragment) {
    supportFragmentManager.beginTransaction().remove(fragment).commitNow()
    supportFragmentManager.popBackStack(fragment.tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}

fun Context.removeFragmentBackstack(fragment: Fragment) {
    this as AppCompatActivity
    supportFragmentManager.beginTransaction().remove(fragment).commitNow()
    supportFragmentManager.popBackStack(fragment.tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}

fun Fragment.removeFragmentBackstack() {
    val activity = this.requireContext() as AppCompatActivity
    activity.supportFragmentManager.beginTransaction().remove(this).commitNow()
    activity.supportFragmentManager.popBackStack(this.tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}

fun AppCompatActivity.removeFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction().remove(fragment).commitNowAllowingStateLoss()
}

fun FragmentManager.removeFragmentWithStateLoss(fragment: Fragment) {
    beginTransaction().remove(fragment).commitNowAllowingStateLoss()
}


fun Context.removeFragment(fragment: Fragment) {
    this as AppCompatActivity
    supportFragmentManager.removeFragmentWithStateLoss(fragment)
}

fun Fragment.removeFragment() {
    val activity = this.requireContext() as AppCompatActivity
    activity.supportFragmentManager.removeFragmentWithStateLoss(this)
}

fun Fragment.removeFragmentChild() {
    childFragmentManager.removeFragmentWithStateLoss(this)
}

fun AppCompatActivity.currentFragment(@IdRes container: Int): Fragment? {
    return supportFragmentManager.findFragmentById(container)
}

fun FragmentActivity.isFragmentAtTheTop(fragment: Fragment): Boolean =
    supportFragmentManager.fragments.last() == fragment

fun AppCompatActivity.isFragmentAtTheTop(fragment: Fragment): Boolean =
    supportFragmentManager.fragments.last() == fragment
/**
 * An extension to `postponeEnterTransition` which will resume after a timeout.
 */
fun Fragment.postponeEnterTransition(timeout: Long) {
    postponeEnterTransition()
    Handler(Looper.getMainLooper()).postDelayed({ startPostponedEnterTransition() }, timeout)
}

val Fragment.viewCoroutineScope get() = viewLifecycleOwner.lifecycle.coroutineScope