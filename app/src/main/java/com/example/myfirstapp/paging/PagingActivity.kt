package com.example.myfirstapp.paging

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.databinding.ActivityPagingBinding
import kotlin.collections.ArrayList

class PagingActivity : AppCompatActivity() {
    lateinit var binding: ActivityPagingBinding
    lateinit var viewModel: PagingViewModel
    lateinit var adapter: PagingRecycleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewmodel()
        setupRecyclerview()
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            setObserver()
        },3000)


    }

    private fun setObserver() {
        viewModel.getPosts("https://reqres.in/api/users/1").observe(this, { data ->
            Log.e(TAG, "setObserver: "+data )
            val arrayList = ArrayList<PagingDataClass>()
            arrayList.add(data!!)
            adapter.list = arrayList
            adapter.notifyDataSetChanged()
        })
    }

    private fun setupRecyclerview() {
        binding.mRecycler.layoutManager = LinearLayoutManager(this)
        adapter = PagingRecycleAdapter(ArrayList<PagingDataClass>())
    }

    private fun setupViewmodel() {
        viewModel = ViewModelProvider(this, PagingFactory(PagingService.getApiService())) [PagingViewModel::class.java]
    }
}