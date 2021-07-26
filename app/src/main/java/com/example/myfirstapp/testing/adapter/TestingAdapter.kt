package com.example.myfirstapp.services.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.FragmentLayoutBinding
import com.example.myfirstapp.databinding.ImageLayoutBinding
import com.example.myfirstapp.databinding.TestingAdapterLayoutBinding
import com.example.myfirstapp.mvvm.adapter.Imageadapter
import com.example.myfirstapp.mvvm.model.Mymodel
import com.example.myfirstapp.services.Pojo
import com.example.myfirstapp.testing.adapter.RecyclerGridAdapter
import java.lang.ref.WeakReference

data class TestingModel(val name:String)
class TestingAdapter(var mList: ArrayList<TestingModel> , var imageList: ArrayList<Mymodel>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val TYPE1 = 1
    val TYPE2 = 2
    private lateinit var gridAdapter: RecyclerGridAdapter
    private lateinit var ctx: Context

    class Myviewholder(val binding: TestingAdapterLayoutBinding) : RecyclerView.ViewHolder(binding.root)
//    class Myviewholder2(val binding: ImageLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE2){
            ctx = parent.context
            gridAdapter = RecyclerGridAdapter()
//            return Myviewholder2(
//                ImageLayoutBinding.inflate(
//                    LayoutInflater.from(parent.context),parent,false))
            return ListViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.recycle_grid, parent, false)
            )
        }
        return Myviewholder(
            TestingAdapterLayoutBinding.inflate(
                LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == TYPE2){
            (holder as ListViewHolder).bind(imageList,gridAdapter,ctx)
//            (holder as Myviewholder2).binding.imageView.setImageResource(imageList[position].image)

        }else{
            (holder as Myviewholder).binding.name.text = mList[position].name
        }
    }
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recyclerGridView = itemView.findViewById<RecyclerView>(R.id.recyclerGrid)

        fun bind(data: List<Mymodel>, adapter: RecyclerGridAdapter, ctx: Context) {

            recyclerGridView.layoutManager = GridLayoutManager( ctx, 1, GridLayoutManager.HORIZONTAL, false)
            recyclerGridView.adapter = adapter
            adapter.setContent(data)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (mList.size-1 == position){
            return TYPE2
        }
        return TYPE1
    }
}