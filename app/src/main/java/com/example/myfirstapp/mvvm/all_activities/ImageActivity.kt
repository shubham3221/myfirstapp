package com.example.myfirstapp.mvvm.all_activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.*
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.mvvm.adapter.Imageadapter
import com.example.myfirstapp.mvvm.model.Mymodel
import com.example.myfirstapp.mvvm.viewmodel.Myviewmodel
import com.example.myfirstapp.mvvm.viewmodel.Status
import com.example.myfirstapp.paging.PagingActivity
import com.example.myfirstapp.extra.PaginationScrollListener
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ImageActivity : AppCompatActivity() {
    lateinit var madapter: Imageadapter
    lateinit var observer: MyLifecycleObserver
    lateinit var viewModel: Myviewmodel
    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        observer = MyLifecycleObserver(activityResultRegistry,this)
        lifecycle.addObserver(observer)
//        startActivity(Intent(this,RoomActivity::class.java))


        viewModel = ViewModelProvider(this).get(Myviewmodel::class.java)

        setuprecyclerview()
        setObserver()
        setRetrofitObserver()

        text.movementMethod = ScrollingMovementMethod()

        room_act.setOnClickListener {
//            startActivity(Intent(this, RoomActivity::class.java))
            startActivity(Intent(this, PagingActivity::class.java))
        }

        button.setOnClickListener {
            viewModel.addImages(20)
            text.text = "count: ${viewModel.repositoryTesting.liveData.value!!.size}"
            return@setOnClickListener

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
                it?.let {
                    imageView.setImageURI(it)
                    var file: File? = Utils.getRealPathAllinOne(this,uri = it)
//                    file = try {
//                        File(Utils().getRealPathFromRecent(this, it))
//                    }catch (e:Exception){
//                        File(Utils().getRealPathFromURI2(this, it))
//                    }
//                    val filePart = MultipartBody.Part.createFormData("image", file.name, file.asRequestBody())
                    val frontSideImage = MultipartBody.Part.createFormData("image",
                        file!!.name,
                        file.asRequestBody("multipart/form-data".toMediaTypeOrNull()))

                    viewModel.uploadImage(frontSideImage).observe(this, { result ->
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
        var requestPermission: ActivityResultLauncher<Array<String>>? = null
        requestPermission =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: MutableMap<String, Boolean>? ->
                var isGranted = true
                permissions?.forEach {
                    if (!it.value) {
                        isGranted = false
                    }
                }
                if (isGranted) getContent.launch("image/*") else text.text = "Permission not granted"
            }
        launch_fragment.setOnClickListener {

            requestPermission.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE))
//            observer.getPermission("d",object : PermissionListener{
//                override fun result(result: Boolean) {
//
//                }
//            })
//            observer.getPermission()

            //launch fragment
//                supportFragmentManager.beginTransaction().add(R.id.container, BlankFragment())
//                    .addToBackStack(null)
//                    .commit()
        }
    }

    fun requestPermission(requestPermission: ActivityResultLauncher<Array<String>>) {
        requestPermission.launch(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    private fun setuprecyclerview() {
        madapter = Imageadapter(ArrayList<Mymodel>())
        val linearLayoutManager = LinearLayoutManager(this)
        mRecycler.layoutManager = linearLayoutManager
        mRecycler.adapter = madapter
        mRecycler.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun isLastPage(): Boolean {
                Log.e(TAG, "isLastPage: ")
                return isLastPage
            }

            override fun isLoading(): Boolean {
                Log.e(TAG, "isLoading: ")
                return isLoading
            }

            override fun loadMoreItems() {
                if (!isLoading) {
                    isLoading = true
                    getMoreItems()
                }
                Log.e(TAG, "loadMoreItems: ")
            }

            private fun getMoreItems() {
                lifecycleScope.launch {
                    text.text = "loading..."
                    delay(5000)
                    viewModel.addImages(10)
                    isLoading = false
                    text.text = "count: ${viewModel.repositoryTesting.liveData.value!!.size}"

                }
            }

        })
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

        viewModel.repositoryTesting.liveData.observe(this, {
            madapter.updateAdapter(it)
        })

//        viewModel.repo.fetchPosts(10).observe(this, Observer {
//            Log.e(TAG, "setObserver: api ${it.size}")
//        })
//
//        viewModel.getStatus().observe(this, Observer {
////            Log.e(TAG, "status: $it")
//        })


    }


}
