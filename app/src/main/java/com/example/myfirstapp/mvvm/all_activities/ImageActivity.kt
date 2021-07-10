package com.example.myfirstapp.mvvm.all_activities

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.example.myfirstapp.mvvm.viewmodel.Myviewmodel
import com.example.myfirstapp.mvvm.viewmodel.Status
import kotlinx.android.synthetic.main.activity_image.*


class ImageActivity : AppCompatActivity() {
    lateinit var madapter: Imageadapter
//    lateinit var observer: MyLifecycleObserver
    lateinit var viewModel: Myviewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
//        observer = MyLifecycleObserver(activityResultRegistry)
//        lifecycle.addObserver(observer)
        viewModel = ViewModelProvider(this).get(Myviewmodel::class.java)

//        madapter = Imageadapter(ArrayList<Mymodel>())
//        mRecycler.layoutManager = GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false)
//        mRecycler.adapter = madapter

//        setObserver()
        setRetrofitObserver()

        text.movementMethod = ScrollingMovementMethod()

        button.setOnClickListener {
            if (edit_query.text.isEmpty()) {
                //post request
                post()
            } else {
                //get request
                viewModel.fetchSpecificPost(edit_query.text.toString().toInt())
                    .observe(this, Observer {
                        when (it.status) {
                            Status.SUCCESS -> {
                                Log.e(TAG, "success: ")
                            }
                            Status.ERROR -> {
                                text.text = it.message
                            }
                            Status.LOADING -> {
                                text.text = "Loading..."
                            }
                        }
                    })
            }


//            viewModel.getSpecificPost(edit_query.text.toString().toInt()).observe(this, Observer {
//                when (it.status) {
//                    Status.SUCCESS -> {
//                        Log.e(TAG, "onCreate: " )
//                    }
//                    Status.ERROR -> {
//                        text.text = it.message
//                    }
//                    Status.LOADING -> {
//                        text.text = "Loading..."
//                    }
//                }
//            })
        }
        val getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                text.text = it.toString()

//                it?.let {
//                    val file = File(it.path)
//                    Log.e(TAG, "onCreate: " + file.absolutePath)
//                    Log.e(TAG, "onCreate: " + file.absoluteFile)
//                    val requestFile =
//                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
//                    val frontSideImage =
//                        MultipartBody.Part.createFormData("profile_image", file.name, requestFile)
//                    viewModel.uploadImage(frontSideImage).observe(this, Observer { result ->
//                        when (result.status) {
//                            Status.SUCCESS -> {
//                                text.text = result.data.toString()
//                            }
//                            Status.ERROR -> {
//                                text.text = "Error: " + result.message
//                            }
//                            Status.LOADING -> {
//                                text.text = "loading..."
//                            }
//                        }
//                    })
//                }
            }
        launch_fragment.setOnClickListener {
            getContent.launch("image/*")
//            observer.getPermission()
//            val commitAllowingStateLoss =
//                supportFragmentManager.beginTransaction().add(R.id.container, BlankFragment())
//                    .addToBackStack(null)
//                    .commit()
        }
    }
    fun getRealPathFromURI(contentUri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = getContentResolver().query(contentUri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }

    private fun post() {
        viewModel.postRequest().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    text.text = it.data.toString()
                }
                Status.ERROR -> {
                    text.text = it.message.toString()
                }
                Status.LOADING -> {
                    text.text = "loading..."
                }
            }
        })
    }

    private fun setRetrofitObserver() {

        viewModel.repositoryTesting.specificpostList.observe(this, Observer {
            Log.e(TAG, "setRetrofitObserver: ")
            text.text = it.toString()
        })

        viewModel.liveList.observe(this, Observer { mydata ->
            text.text = mydata.toString()
            Log.e(TAG, "setRetrofitObserver:data ")
        })

        viewModel.repositoryTesting.status.observe(this, Observer {
            if (it) text.text = "loading..."
            Log.e(TAG, "setRetrofitObserver:status ")
        })
    }

    private fun setObserver() {


//        viewModel.liveData.observe(this, Observer {
//            Log.e(TAG, "observer called: ")
//            madapter.updateAdapter(it)
//        })

//        viewModel.repo.fetchPosts(10).observe(this, Observer {
//            Log.e(TAG, "setObserver: api ${it.size}")
//        })
//
//        viewModel.getStatus().observe(this, Observer {
////            Log.e(TAG, "status: $it")
//        })


    }


}
