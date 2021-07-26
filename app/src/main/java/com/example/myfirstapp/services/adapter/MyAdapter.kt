package com.example.myfirstapp.services.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.databinding.FragmentLayoutBinding
import com.example.myfirstapp.services.Pojo

class MyAdapter(private val items: ArrayList<Pojo>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentLayoutBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(val binding: FragmentLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pojo) {
            binding.name = item.name
            binding.number = item.number
            binding.executePendingBindings()
        }
    }

    fun update(){
        notifyDataSetChanged()
    }
}