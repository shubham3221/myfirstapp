package com.example.myfirstapp.paging

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.databinding.ActivityPagingBinding
import com.example.myfirstapp.extra.PaginationScrollListener
import kotlin.collections.ArrayList

class PagingActivity : AppCompatActivity() {
    lateinit var binding: ActivityPagingBinding
    lateinit var viewModel: PagingViewModel
    lateinit var adapter: PagingAdapter
    var list = ArrayList<PagingDataClass>()
    var list2 = ArrayList<PagingDataClass>()
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
        viewModel.getPosts("https://reqres.in/api/users/1").observe(this, { data ->
            list.add(data)
            adapter.updateAdapter(list)
        })
    }

    private fun setupRecyclerview() {
        linearLayoutManager = LinearLayoutManager(this)
        binding.mRecycler.layoutManager =linearLayoutManager
        adapter = PagingAdapter(list2)

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