package com.example.myfirstapp.paging

import android.content.Context
import android.util.Log
import com.example.myfirstapp.RecyclerUtils.AbstractViewBindingCleanAdapter
import com.example.myfirstapp.databinding.PagingRecycleLayoutBinding

class PagingAdapter(val arrayList: ArrayList<Any>) : AbstractViewBindingCleanAdapter<PagingDataClass, PagingRecycleLayoutBinding>(
    PagingRecycleLayoutBinding::inflate) {
    override fun bindItems(
        item: PagingDataClass,
        position: Int,
        itemCount: Int,
        binding: PagingRecycleLayoutBinding,
        context: Context,
    ) {
        TODO("Not yet implemented")
    }

    override fun onCreateBinding(holder: AbstractViewHolder<PagingRecycleLayoutBinding>) {
        TODO("Not yet implemented")
    }
}
class PagingAdapter2(var arrayList: ArrayList<PagingDataClass>?) :
    PagingRecycleAdapter2<PagingRecycleLayoutBinding>(
    arrayList,PagingRecycleLayoutBinding::inflate){

    override fun bindView(holder: AbstractViewHolder2<PagingRecycleLayoutBinding>, position: Int) {
        Log.e("//", "bindView: "+arrayList!!.size )
        if (!arrayList.isNullOrEmpty()){
            holder.binding.textView2.text = arrayList!![position].data.email
        }
    }
}