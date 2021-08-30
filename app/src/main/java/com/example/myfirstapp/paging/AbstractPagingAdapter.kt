package com.example.myfirstapp.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class AbstractPagingAdapter<VB : ViewBinding >(
    var list: ArrayList<*>,
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
) :
    RecyclerView.Adapter<AbstractPagingAdapter.AbstractViewHolder2<VB>>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder2<VB> {
        val binding = bindingInflater.invoke(LayoutInflater.from(parent.context), parent, false)
        val holder = AbstractViewHolder2(binding)
        //attach click listeners etc...

        return holder
    }

    override fun onBindViewHolder(holder: AbstractViewHolder2<VB>, position: Int) {
        bindView(holder,position)
    }
    abstract fun bindView(holder: AbstractViewHolder2<VB>, position: Int)

    override fun getItemCount(): Int {
        return list.size
    }
    fun <T> updateAdapter(list: ArrayList<T>){
        this.list.clear()
        this.list = list
        notifyDataSetChanged()
    }

    class AbstractViewHolder2<VB : ViewBinding>(val binding: VB) :
        RecyclerView.ViewHolder(binding.root)
}