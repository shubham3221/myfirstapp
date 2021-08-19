package com.example.myfirstapp.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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