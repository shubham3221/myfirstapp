package com.example.myfirstapp.paging

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.databinding.ActivityPagingBinding
import com.example.myfirstapp.utils.PaginationScrollListener
import kotlin.collections.ArrayList

class PagingActivity : AppCompatActivity() {
    lateinit var binding: ActivityPagingBinding
    lateinit var viewModel: PagingViewModel
    lateinit var adapter: PagingAdapter2
    var list = ArrayList<PagingDataClass>()
    lateinit var linearLayoutManager: LinearLayoutManager
    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewmodel()
        setupRecyclerview()
        setObserver()

    }

    private fun setObserver() {
//        viewModel.getPosts("https://reqres.in/api/users/1").observe(this, { data ->
//            val g = ArrayList<PagingDataClass>()
//            Log.e(TAG, "setObserver: "+g.get(0).data.email )
//            adapter.updateAdapter(g)
//        })

        viewModel.getPosts2("https://reqres.in/api/users/1"){
            Thread.sleep(1000)
            list.add(it)
            adapter.updateAdapter(list)
        }
    }

    private fun setupRecyclerview() {
        linearLayoutManager = LinearLayoutManager(this)
        binding.mRecycler.layoutManager =linearLayoutManager
        list = ArrayList()
        list.add(PagingDataClass(SubData(1,"efef","Sdf","sdf","Sdff")))
        adapter = PagingAdapter2(list)
        binding.mRecycler.adapter = adapter



        binding.mRecycler.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun isLastPage(): Boolean {
                Log.e(TAG, "isLastPage: ")
                return isLastPage
            }

            override fun isLoading(): Boolean {
                Log.e(TAG, "isLoading: ")
                return isLoading
            }

            override fun loadMoreItems() {

            }

        })
    }

    private fun setupViewmodel() {
        viewModel = ViewModelProvider(this, PagingFactory(PagingService.getApiService())) [PagingViewModel::class.java]
    }
}