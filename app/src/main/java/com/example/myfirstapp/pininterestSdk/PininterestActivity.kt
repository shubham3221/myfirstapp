package com.example.myfirstapp.pininterestSdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.VolleyError
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.pinterest.android.pdk.*
import kotlinx.android.synthetic.main.activity_pininterest.*
import java.util.*

class PininterestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pininterest)

        PDKClient.configureInstance(this, getString(R.string.PIN_ID));
        PDKClient.getInstance().onConnect(this,object :PDKCallback(){
            override fun onErrorResponse(error: VolleyError?) {
                super.onErrorResponse(error)
                Log.e(TAG, "onFailure: " + error?.message)

            }

            override fun onFailure(exception: PDKException?) {
                Log.e(TAG, "onFailure: " + exception?.message)
                super.onFailure(exception)
            }

            override fun onSuccess(response: PDKResponse?) {
                Log.e(TAG, "onSuccess: "+response.toString())
                super.onSuccess(response)

            }
        })
        pinlogin.setOnClickListener {
            PDKClient.getInstance().login(this,
                Arrays.asList("username", "name"),
                object : PDKCallback() {
                    override fun onSuccess(response: PDKResponse?) {
                        super.onSuccess(response)
                        Log.e(TAG, "onSuccess: "+response.toString())
                    }

                    override fun onFailure(exception: PDKException?) {
                        super.onFailure(exception)
                        Log.e(TAG, "onFailure: " + exception?.message)
                    }

                    override fun onErrorResponse(error: VolleyError?) {
                        super.onErrorResponse(error)
                        Log.e(TAG, "onErrorResponse: "+error?.message )
                    }
                })
        }
    }
}