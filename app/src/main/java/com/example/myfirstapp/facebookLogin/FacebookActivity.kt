package com.example.myfirstapp.facebookLogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.facebook.*
import com.facebook.AccessToken
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.activity_facebook.*
import org.json.JSONException
import java.util.*


class FacebookActivity : AppCompatActivity() {
     lateinit var callbackManager: CallbackManager
     lateinit var loginManager: LoginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facebook)

//        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends","email"))

        callbackManager = CallbackManager.Factory.create()

        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                Log.e(TAG, "onSuccess: " + loginResult!!.accessToken.token)
                val currentProfile = Profile.getCurrentProfile()
            }

            override fun onCancel() {
                Log.e(TAG, "onCancel: ")
                // App code
            }

            override fun onError(exception: FacebookException) {
                Log.e(TAG, "onError: " + exception.message)
                // App code
            }
        })

        button2.setOnClickListener {
            getFbInfo()
        }

    }
    //
    private fun getFbInfo() {
        val request = GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken()
        ) { `object`, response ->
            try {
                 `object`?.let {
                     info.text = it.toString()
                }
                Log.d(TAG, "fb json object: $`object`")
                Log.d(TAG, "fb graph response: $response")
                val id = `object`.getString("id")
                val first_name = `object`.getString("first_name")
                val last_name = `object`.getString("last_name")
                val gender = `object`.getString("gender")
                val birthday = `object`.getString("birthday")
                val image_url = "http://graph.facebook.com/$id/picture?type=large"
                val email: String
                if (`object`.has("email")) {
                    email = `object`.getString("email")
                }
            } catch (e: JSONException) {
                Log.e(TAG, "getFbInfo: " + e.message)
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields",
            "id,first_name,last_name,email,gender,birthday") // id,first_name,last_name,email,gender,birthday,cover,picture.type(large)
        request.parameters = parameters
        request.executeAsync()
    }
    fun secondMethod(){
        val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()
        ) { `object`, response ->
            try {
                Log.i("RESAULTS : ", `object`.getString("email"))
                info.text = `object`.toString()
            } catch (e: Exception) {
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "email")
        request.parameters = parameters
        request.executeAsync()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e(TAG, "onActivityResult: ")
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)
    }
}