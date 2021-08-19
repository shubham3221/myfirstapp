package com.example.myfirstapp.extra

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.myfirstapp.recyclerUtils.AbstractViewBindingHolderAdapter
import com.example.myfirstapp.recyclerUtils.orientation
inline fun <reified T, VB : ViewBinding> generateRecyclerWithHolder(
    noinline bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    noinline areItemsTheSameCallback: (old: T, new: T) -> Boolean? = { _, _ -> null },
    noinline areContentsTheSameCallback: (old: T, new: T) -> Boolean? = { _, _ -> null },
    noinline onCreateBinding: (holder: AbstractViewBindingHolderAdapter.AbstractViewHolder<VB>) -> Unit = {},
    crossinline binder: (item: T, position: Int, itemCount: Int, binding: VB, context: Context) -> Unit): AbstractViewBindingHolderAdapter<T, VB> {

    return object : AbstractViewBindingHolderAdapter<T, VB>(bindingInflater, areItemsTheSameCallback, areContentsTheSameCallback, onCreateBinding) {
        override fun bindItems(item: T, position: Int, itemCount: Int, binding: VB, context: Context) {
            binder(item, position, itemCount, binding, context)
        }
    }
}
class RecycleExtenstions {
    /**
     * Sets a linear layout manager along with an adapter
     */
    fun RecyclerView.withLinearAdapter(rvAdapter: RecyclerView.Adapter<*>) = apply {
        layoutManager = LinearLayoutManager(context)
        adapter = rvAdapter
    }
    val <VH> RecyclerView.Adapter<VH>.isEmpty: Boolean where VH : RecyclerView.ViewHolder
        get() = itemCount == 0


    val <VH> RecyclerView.Adapter<VH>.isNotEmpty: Boolean where VH : RecyclerView.ViewHolder
        get() = !isEmpty

    /**
     * Disable all user input to a recyclerview, passing touch events out
     */
    fun RecyclerView.disableTouch() {
        this.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return true
            }
        })
    }
    fun RecyclerView.divider(color: Int = Color.parseColor("#CCCCCC"), size: Int = 1): RecyclerView {
        val decoration = DividerItemDecoration(context, orientation)
        decoration.setDrawable(GradientDrawable().apply {
            setColor(color)
            shape = GradientDrawable.RECTANGLE
            val dpSize = dp2px(size)
            dpSize?.let {
                setSize(dpSize, dpSize)
            }
        })
        addItemDecoration(decoration)
        return this
    }
    private fun View.dp2px(dpValue: Int): Int? {
        return context?.dp2px(dpValue)
    }

    private fun Context.dp2px(dpValue: Int): Int {
        return (dpValue * resources.displayMetrics.density + 0.5f).toInt()
    }

    fun RecyclerView.enableAnimations(addDuration: Long = 120, removeDuration: Long = 120, moveDuration: Long = 250, changeDuration: Long = 250) {
        this.itemAnimator?.addDuration = addDuration
        this.itemAnimator?.removeDuration = removeDuration
        this.itemAnimator?.moveDuration = moveDuration
        this.itemAnimator?.changeDuration = changeDuration
    }
    fun RecyclerView.disableAnimations() {
        this.itemAnimator?.addDuration = 0
        this.itemAnimator?.removeDuration = 0
        this.itemAnimator?.moveDuration = 0
        this.itemAnimator?.changeDuration = 0
    }
}