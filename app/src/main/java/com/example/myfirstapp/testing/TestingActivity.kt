package com.example.myfirstapp.testing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.ActivityServiceBinding
import com.example.myfirstapp.databinding.ActivityTestingBinding
import com.example.myfirstapp.mvvm.model.Mymodel
import com.example.myfirstapp.services.adapter.TestingAdapter
import com.example.myfirstapp.services.adapter.TestingModel

class TestingActivity : AppCompatActivity() {
    lateinit var binding: ActivityTestingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)
        binding = ActivityTestingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setuplist
        var list1 = ArrayList<TestingModel>()
        list1.add(TestingModel("shubham"))
        list1.add(TestingModel("arun"))
        list1.add(TestingModel("sahil"))
        list1.add(TestingModel("prabhu"))
        list1.add(TestingModel("prabhu"))
        list1.add(TestingModel("prabhu"))
        list1.add(TestingModel("prabhu"))
        list1.add(TestingModel("prabhu"))
        list1.add(TestingModel("prabhu"))
        list1.add(TestingModel("prabhu"))
        list1.add(TestingModel("prabhu"))
        list1.add(TestingModel("prabhu"))
        list1.add(TestingModel("prabhu"))
        list1.add(TestingModel("prabhu"))
        list1.add(TestingModel("prabhu"))
        list1.add(TestingModel("prabhu"))
        list1.add(TestingModel("prabhu"))
        list1.add(TestingModel("prabhu"))
        list1.add(TestingModel("prabhu"))

        var list2 = ArrayList<Mymodel>()
        list2.add(Mymodel(R.drawable.ic_baseline_star_24))
        list2.add(Mymodel(R.drawable.ic_baseline_star_24))
        list2.add(Mymodel(R.drawable.ic_baseline_star_24))
        list2.add(Mymodel(R.drawable.ic_baseline_star_24))


        //setRecyclerView
        binding.mRecycler.layoutManager = LinearLayoutManager(this)
        binding.mRecycler.adapter = TestingAdapter(list1,list2)

    }
}