package com.example.myfirstapp.RecyclerUtils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class AbstractViewBindingAdapter<T, VH : RecyclerView.ViewHolder, VB : ViewBinding>(
        private val viewHolder: (binding: VB) -> VH,
        private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
        areItemsTheSameCallback: (old: T, new: T) -> Boolean? = { _, _ -> null },
        areContentsTheSameCallback: (old: T, new: T) -> Boolean? = { _, _ -> null },
        private val onCreateBinding: (holder: VH) -> Unit = {}

) :
        ListAdapter<T, VH>(GenericDiffUtil(areItemsTheSameCallback, areContentsTheSameCallback)) {
    abstract fun bindItems(item: T, holder: VH, position: Int, itemCount: Int)

    var forItemClickListener: forItemClickListener<T>? = null
    var onLongClickListener: forItemClickListener<T>? = null

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item: T = getItem(holder.bindingAdapterPosition)
        bindItems(item, holder, position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = bindingInflater.invoke(LayoutInflater.from(parent.context), parent, false)
        val holder = setViewHolder(binding)
        onCreateBinding(holder)

        holder.itemView.setOnClickListenerCooldown {
            if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION)
                forItemClickListener?.forItem(holder.bindingAdapterPosition, getItem(holder.bindingAdapterPosition), it)
        }
        holder.itemView.setOnLongClickListener {
            if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION)
                onLongClickListener?.forItem(holder.bindingAdapterPosition, getItem(holder.bindingAdapterPosition), it)
            true
        }
        return holder
    }

    @Suppress("UNCHECKED_CAST")
    private fun setViewHolder(binding: ViewBinding): VH = viewHolder(binding as VB)
}