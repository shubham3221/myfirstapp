package com.example.myfirstapp.paging

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.myfirstapp.RecyclerUtils.AbstractViewBindingCleanAdapter
import com.example.myfirstapp.databinding.PagingRecycleLayoutBinding

class PagingRecycleAdapter(var list: ArrayList<PagingDataClass>) :
    RecyclerView.Adapter<PagingRecycleAdapter.MyPagingViewHolder>() {

    class MyPagingViewHolder(val binding: PagingRecycleLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyPagingViewHolder(PagingRecycleLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: MyPagingViewHolder, position: Int) {
        holder.binding.textView2.text = list[position].data.email
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateAdapter(list2: ArrayList<PagingDataClass>) {
        this.list = list2
        notifyDataSetChanged()
    }
}

abstract class PagingRecycleAdapter2<VB : ViewBinding>(
    var list: ArrayList<*>?,
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
) :
    RecyclerView.Adapter<PagingRecycleAdapter2.AbstractViewHolder2<VB>>() {


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
        list?.let {
            return it.size
        }
        Log.e("//", "getItemCount: " )
        return 0
    }

    fun updateAdapter(list2: ArrayList<*>) {
        this.list = list2
        notifyDataSetChanged()
    }

    class AbstractViewHolder2<VB : ViewBinding>(val binding: VB) :
        RecyclerView.ViewHolder(binding.root)
}