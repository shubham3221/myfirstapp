package com.example.myfirstapp.testing.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.Modelclass
import com.example.myfirstapp.R
import com.example.myfirstapp.mvvm.model.Mymodel

class RecyclerGridAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var items: List<Mymodel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GridViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.image_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GridViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    fun setContent(items: List<Mymodel>) {
        this.items = items
    }


    override fun getItemCount(): Int {
        return items.size
    }

    class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val emailLabel = itemView.findViewById<ImageView>(R.id.imageView)

        fun bind(user: Mymodel) {
            emailLabel.setImageResource(user.image)
        }
    }

}