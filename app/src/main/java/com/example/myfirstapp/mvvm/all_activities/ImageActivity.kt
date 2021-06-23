package com.example.myfirstapp.mvvm.all_activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.example.myfirstapp.mvvm.model.Mymodel
import com.example.myfirstapp.mvvm.model.Postmodel
import com.example.myfirstapp.mvvm.viewmodel.Myviewmodel
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {
    lateinit var madapter:Imageadapter
    lateinit var viewModel:Myviewmodel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        viewModel = ViewModelProvider(this).get(Myviewmodel::class.java)

        madapter = Imageadapter(ArrayList<Mymodel>())
        mRecycler.layoutManager = GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false)
        mRecycler.adapter = madapter


        setObserver()
        setRetrofitObserver()

        text.movementMethod = ScrollingMovementMethod()


        button.setOnClickListener {
            viewModel.getPosts(1)
//            viewModel.addImages(4)
        }


    }

    private fun setRetrofitObserver() {
        viewModel.liveList.observe(this, Observer {
            text.text = it.toString()
            Log.e(TAG, "setRetrofitObserver:data " )
        })

        viewModel.repo.status.observe(this, Observer {
            if (it) text.text = "loading..."
            Log.e(TAG, "setRetrofitObserver:status " )
        })

    }

    private fun setObserver() {
        viewModel.repo.liveData.observe(this, Observer {
            Log.e(TAG, "setObserver: "+it.size )
            madapter.updateList(it)
        })

//        viewModel.addImages(4).observe(this, Observer {
//            madapter.updateList(it)
//            Log.e(TAG, "second: "+it.size )
//        })
//
//        viewModel.getPosts().observe(this, Observer {
//            Log.e(TAG, "setObserver: api ${it.size}")
//        })
//
//        viewModel.getStatus().observe(this, Observer {
////            Log.e(TAG, "status: $it")
//        })


    }


}