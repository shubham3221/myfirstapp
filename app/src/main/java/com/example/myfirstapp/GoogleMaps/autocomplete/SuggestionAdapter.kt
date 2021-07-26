package com.example.myfirstapp.GoogleMaps.autocomplete

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.myfirstapp.R


class SuggestionAdapter(context: Context, resource: Int, var dataList: ArrayList<String>?) :
    ArrayAdapter<Any?>(context, resource) {
    private val mContext: Context = context
    private val itemLayout: Int
    private val listFilter: ListFilter = ListFilter()
    private var dataListAllItems: ArrayList<String>? = null
    override fun getCount(): Int {
        return dataList!!.size
    }

    override fun getItem(position: Int): String? {
        return dataList!![position]
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view: View? = view
        if (view == null) {
            view = LayoutInflater.from(parent.context)
                .inflate(itemLayout, parent, false)
        }
        val strName = view!!.findViewById(R.id.searchFullText) as TextView
        strName.text = getItem(position)
        return view
    }

    override fun getFilter(): Filter {
        return listFilter
    }

    inner class ListFilter : Filter() {
        private val lock = Any()
         override fun performFiltering(prefix: CharSequence?): FilterResults {
            val results = FilterResults()
            if (dataListAllItems == null) {
                synchronized(lock) { dataListAllItems = ArrayList(dataList) }
            }
            if (prefix == null || prefix.isEmpty()) {
                synchronized(lock) {
                    results.values = dataListAllItems
                    results.count = dataListAllItems!!.size
                }
            } else {
                val searchStrLowerCase = prefix.toString().toLowerCase()
                val matchValues = ArrayList<String>()
                for (dataItem in dataListAllItems!!) {
                    if (dataItem.toLowerCase().startsWith(searchStrLowerCase)) {
                        matchValues.add(dataItem)
                    }
                }
                results.values = matchValues
                results.count = matchValues.size
            }
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            dataList = if (results.values!= null){
                results.values as ArrayList<String>
            }else{
                null
            }

            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }

    init {
        itemLayout = resource
    }
    fun updateList(list: ArrayList<String>){
        this.dataList = list
        notifyDataSetChanged()
    }
}