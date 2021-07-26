package com.example.myfirstapp.paging

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.RecyclerUtils.AbstractViewBindingCleanAdapter
import com.example.myfirstapp.databinding.PagingRecycleLayoutBinding

class PagingRecycleAdapter(var list: ArrayList<PagingDataClass>) : RecyclerView.Adapter<PagingRecycleAdapter.MyPagingViewHolder>() {

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