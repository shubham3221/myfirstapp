package com.example.myfirstapp.paging

import com.example.myfirstapp.databinding.PagingRecycleLayoutBinding
class PagingAdapter(var arrayList: ArrayList<PagingDataClass>) : AbstractPagingAdapter<PagingRecycleLayoutBinding>(
    arrayList,PagingRecycleLayoutBinding::inflate){

    override fun bindView(holder: AbstractViewHolder2<PagingRecycleLayoutBinding>, position: Int) {
        if (arrayList.isNotEmpty()){
            holder.binding.textView2.text = arrayList[position].data.email
        }
    }
}