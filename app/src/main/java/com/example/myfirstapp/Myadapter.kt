package com.example.myfirstapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class Myadapter(val list: List<Modelclass>) : RecyclerView.Adapter<Myadapter.viewHolder>() {

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imageView)
        val title: TextView = itemView.findViewById(R.id.title)
        val info: TextView = itemView.findViewById(R.id.info)
        val soonLogo:ImageView = itemView.findViewById(R.id.coming_soon)

        init {
            itemView.setOnClickListener {
                Toast.makeText(itemView.context,"hi",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.mylayout, parent, false)
        return viewHolder(mView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        when(position){
            0 -> {
                holder.soonLogo.visibility = View.GONE
            }
            else -> {
                holder.soonLogo.visibility = View.VISIBLE
            }
        }
        holder.image.setImageResource(list.get(position).image)
        holder.title.text = list.get(position).name
        holder.info.text = list.get(position).info

    }

    override fun getItemCount(): Int {
        return list.size
    }
}