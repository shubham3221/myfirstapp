package com.example.myfirstapp.mvvm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.databinding.ImageLayoutBinding
import com.example.myfirstapp.mvvm.model.Mymodel

class Imageadapter(var mList: ArrayList<Mymodel>):RecyclerView.Adapter<Imageadapter.Myviewholder>(){


    class Myviewholder(val binding:ImageLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Myviewholder = Myviewholder(ImageLayoutBinding.inflate(
        LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: Myviewholder, position: Int) {
        holder.binding.imageView.setImageResource(mList[position].image)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateAdapter(list: ArrayList<Mymodel>){
        this.mList = list
        notifyDataSetChanged()
    }
}