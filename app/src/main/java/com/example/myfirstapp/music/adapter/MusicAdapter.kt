package com.example.myfirstapp.music.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.databinding.MusicAdapterLayoutBinding
import com.example.myfirstapp.music.MusicModel

class MusicAdapter(var list:MutableList<MusicModel>): RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    var onItemClick: ((Int , MusicModel)->Unit)? = null

    inner class MusicViewHolder(val binding:MusicAdapterLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        init{
            itemView.setOnClickListener {
                onItemClick?.invoke(adapterPosition , list[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        return MusicViewHolder(MusicAdapterLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.binding.title.text = list[position].name
        holder.binding.artist.text = list[position].artist
        holder.binding.duration.text = list[position].duration
    }

    override fun getItemCount(): Int {
       return list.size
    }

    fun updateAdapter(list: MutableList<MusicModel>){
        this.list = list
        notifyDataSetChanged()
    }
}