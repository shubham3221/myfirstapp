package com.example.myfirstapp.mvvm.all_activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.databinding.ActivityRoomBinding
import com.example.myfirstapp.mvvm.model.AppDatabase
import com.example.myfirstapp.mvvm.model.DatabaseHelperImpl
import com.example.myfirstapp.mvvm.model.EntityClass
import com.example.myfirstapp.mvvm.repo.RoomViewModelNew
import com.example.myfirstapp.mvvm.viewmodel.RoomViewModel
import com.example.myfirstapp.mvvm.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_image.view.*
import kotlinx.coroutines.*

class RoomActivity : AppCompatActivity() {
    lateinit var roomViewModel: RoomViewModel
    lateinit var binding: ActivityRoomBinding
    lateinit var roomViewModel2: RoomViewModelNew
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        roomViewModel = ViewModelProvider(this).get(RoomViewModel::class.java)
        setupViewModel()

        //creating instance of room database


        binding.delete.setOnClickListener {

//            roomViewModel.removeValue(binding.edittext.text.toString().toInt())

            //remove by id
//            roomViewModel2.removeByID(binding.edittext.text.toString().toInt()).observe(this, { result ->
//
//                if (result is Int && result >= 1) makeToast("Success") ?: makeToast(
//                        "Not Found")
//                })

            //remove by name
//            roomViewModel2.removeByName(binding.edittext.text.toString()).observe(this, {
//                Log.e(TAG, "remove by name: "+it.toString())
//            })

            //delete all
            roomViewModel2.deleteAll().observe(this,{
                makeToast(it.toString())
            })
        }

        binding.findbtn.setOnClickListener {
//            if (binding.edittext.text.isNotEmpty()){
//                roomViewModel2.updateUserAsync(18, binding.edittext.text.toString().toInt())
//            }else{
//                roomViewModel2.updateUserAsync(18, 0)
//            }


            roomViewModel2.findByName(binding.edittext.text.toString()).observe(this, {
                if (it.isNotEmpty()) {
                    lifecycleScope.launch {
                            for (i in 0..100) {
                                Log.e(TAG, "onCreate: "+i )
                                delay(50)
                                roomViewModel2.updateUserAsync(it[0].id!!, i)
                            }
                    }
                } else {
                    makeToast("Not Found")
                }
            })


//            roomViewModel2.showOnlyNameEmail().observe(this,{
//                setText(it.toString())
//            })
        }


        binding.insert.setOnClickListener {
//            roomViewModel.insertUsers(EntityClass.User(null,"shubham","email@.com",999))
            roomViewModel2.insertUser(EntityClass.User(null, "shubham", "aemail@.com", 999)).observe(
                this, {
                    Log.e(TAG, "onCreate: " + it)
                })
        }

        binding.update.setOnClickListener {
            roomViewModel.getSpecificUser(binding.edittext.text.toString().toInt())
//            roomViewModel.getUserById(binding.edittext.text.toString().toInt()).observe(this,
//                { user ->
//                    when (user.status) {
//                        Status.LOADING -> {
//                            Log.e(TAG, "onCreate: loading: " + user.message ?: "nullll")
//                            binding.textView.append("loading...")
//                        }
//                        Status.SUCCESS -> {
//                            Log.e(TAG, "onCreate: " )
//                            binding.textView.append("name: ${user.data!!.name}  email: ${user.data.email} \n")
//                        }
//                        Status.ERROR -> {
//                            Log.e(TAG, "onCreate: error: " + user.message ?: "nullll")
//                            binding.textView.append(user.message)
//                        }
//                    }
//                })
        }

//        roomViewModel.users.observe(this,{
//            it?.let { users ->
//                 for ((id,name,email,mobile) in users){
//                     binding.textView.append("id: $id  name: $name \n")
//                 }
//            }
//        })
        roomViewModel.user.observe(this,{
            it?.let { users ->
                binding.textView.text = ("id: ${users.name}  name: ${users.email} \n")
            }
        })

//        // observer all data on main thread
//        roomViewModel.repo.allUsers.observe(this, {
//            binding.textView.text = it.toString()
//        })

        observerData()

    }
    inline fun <reified T> Any?.tryCast(block: T.() -> Unit) {
        if (this is T) {
            block()
        }
    }

    private fun makeToast(result: String) {
        Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
    }

    private fun observerData() {
        roomViewModel2.liveData().observe(this, {
            setText(it.toString())
        })
    }

    private fun setText(text: String) {
        binding.textView.text = text
    }

    private fun setupViewModel() {
        roomViewModel2 = ViewModelProvider(this,
            ViewModelFactory(DatabaseHelperImpl(AppDatabase.OBJECT.getInstance(applicationContext))))
            .get(RoomViewModelNew::class.java)
    }
}