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
import androidx.loader.content.CursorLoader
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.example.myfirstapp.mvvm.viewmodel.Myviewmodel
import com.example.myfirstapp.mvvm.viewmodel.Status
import kotlinx.android.synthetic.main.activity_image.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.jar.Manifest


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
                val realPathFromURI2 = getRealPathFromURI2(it)
                imageView.setImageURI(it)

                it?.let {
                    val file = File(realPathFromURI2)
//                    val filePart = MultipartBody.Part.createFormData("image", file.name, file.asRequestBody())
                    val frontSideImage = MultipartBody.Part.createFormData("image", file.name
                        , file.asRequestBody("multipart/form-data".toMediaTypeOrNull()))
                    viewModel.uploadImage(frontSideImage).observe(this, Observer { result ->
                        when (result.status) {
                            Status.SUCCESS -> {
                                text.text = result.data.toString()
                            }
                            Status.ERROR -> {
                                text.text = "Error: " + result.message
                            }
                            Status.LOADING -> {
                                text.text = "loading..."
                            }
                        }
                    })
                }
            }
        val requestPermission =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: MutableMap<String, Boolean>? ->
                var isGranted = true
                permissions?.forEach{
                    if (!it.value){
                        isGranted = false
                    }
                }
                if (isGranted) getContent.launch("image/*") else text.text = "Permission not granted"
            }
        launch_fragment.setOnClickListener {
            requestPermission.launch(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
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
            val string = cursor.getString(column_index)
            string
        } finally {
            cursor?.close()
        }
    }
    private fun getRealPathFromURI2(contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(this, contentUri, proj, null, null, null)
        val cursor: Cursor = loader.loadInBackground()!!
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result = cursor.getString(column_index)
        cursor.close()
        return result
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
