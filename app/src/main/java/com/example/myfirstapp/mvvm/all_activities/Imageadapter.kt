package com.example.myfirstapp.mvvm.all_activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.mvvm.model.Mymodel

class Imageadapter(var mList: ArrayList<Mymodel>):RecyclerView.Adapter<Imageadapter.Myviewholder>(){


    class Myviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView:ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Myviewholder {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.image_layout, parent, false)
        return Myviewholder(inflate)
    }

    override fun onBindViewHolder(holder: Myviewholder, position: Int) {
        holder.imageView.setImageResource(mList[position].image)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateAdapter(list: ArrayList<Mymodel>){
        this.mList = list
        notifyDataSetChanged()
    }
}