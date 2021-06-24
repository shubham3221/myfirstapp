package com.example.myfirstapp.linkedin

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.kusu.linkedinlogin.Linkedin
import com.kusu.linkedinlogin.LinkedinLoginListener
import com.kusu.linkedinlogin.LinkedinPostResponseListner
import com.kusu.linkedinlogin.model.SocialUser
import com.kusu.linkedinlogin.ui.LinkedinSignInActivity
import kotlinx.android.synthetic.main.activity_loginactivity.*


class Loginactivity : AppCompatActivity() {
    private val CLIENT_ID="78ighxuswbx57a"
    private val SECRET_ID="jM4wxqKUn8stahty"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginactivity)


        Linkedin.initialize(
            context = applicationContext,
            clientId = CLIENT_ID,
            clientSecret = SECRET_ID,
            redirectUri = "https://www.google.com",
            state = "nfw4wfhw44r34fkwq", //For security purpose used to prevent CSRF like-> "nfw4wfhw44r34fkwh"
//            scopes = listOf("r_liteprofile","r_emailaddress","w_member_social")
            scopes = listOf("r_liteprofile", "r_emailaddress")
        )


        linkedin_btn.setOnClickListener {
            Linkedin.login(this, object : LinkedinLoginListener, LinkedinPostResponseListner {
                override fun successLinkedInLogin(socialUser: SocialUser) {
                    Log.e("//", "successLinkedInLogin: " + socialUser.email)
                    Log.e("//", "successLinkedInLogin: " + socialUser.firstName)
                    Log.e("//", "successLinkedInLogin: " + socialUser.lastName)
                    Log.e("//", "successLinkedInLogin: " + socialUser.linkedinToken)
                    Log.e("//", "successLinkedInLogin: " + socialUser.profilePicture)
                    Log.e("//", "successLinkedInLogin: " + socialUser.socialId)
                    linkedin_text.text =
                        "name: ${socialUser.firstName} \n lastname:${socialUser.lastName}\n email: ${socialUser.email}" +
                                "\n socialID: ${socialUser.socialId} "
                }

                override fun failedLinkedinLogin(error: String) {
                    Log.e(TAG, "failedLinkedinLogin: " + error)
                    linkedin_text.text = error
                }

                override fun linkedinPostFailed(error: String) {
                    Log.e(TAG, "linkedinPostFailed: "+error )
                }

                override fun linkedinPostSuccess() {
                    Log.e(TAG, "success: " )
                    TODO("Not yet implemented")
                }

            })
        }
        demo("www.google.com"){result: Boolean ->

        }

    }
    fun demo(url:String , callback: (result:Boolean) -> Unit ){
        callback(false)
    }
}