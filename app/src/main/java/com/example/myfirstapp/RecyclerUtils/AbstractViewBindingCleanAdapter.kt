package com.example.myfirstapp.RecyclerUtils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class AbstractViewBindingCleanAdapter<T, VB : ViewBinding>(
        private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
        areItemsTheSameCallback: (old: T, new: T) -> Boolean? = { _, _ -> null },
        areContentsTheSameCallback: (old: T, new: T) -> Boolean? = { _, _ -> null }
) : ListAdapter<T, AbstractViewBindingCleanAdapter.AbstractViewHolder<VB>>(GenericDiffUtil(areItemsTheSameCallback, areContentsTheSameCallback)) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<VB> {
        val binding = bindingInflater.invoke(LayoutInflater.from(parent.context), parent, false)
        val holder = AbstractViewHolder(binding)
        //attach click listeners etc...
        onCreateBinding(holder)
        return holder
    }

    override fun onBindViewHolder(holder: AbstractViewHolder<VB>, position: Int) {
        val item = getItem(holder.bindingAdapterPosition)
        bindItems(item, position, itemCount, holder.binding, holder.itemView.context)
    }

    abstract fun bindItems(item: T, position: Int, itemCount: Int, binding: VB, context: Context)
    abstract fun onCreateBinding(holder: AbstractViewHolder<VB>)

    class AbstractViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)
}