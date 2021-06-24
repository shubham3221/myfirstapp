package com.example.myfirstapp.twitterLogin

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import kotlinx.android.synthetic.main.activity_twitter.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import twitter4j.conf.ConfigurationBuilder

interface Myinterface{
    fun onClick()
    fun onClickParams(name:String)
}

class TwitterActivity : AppCompatActivity(),Myinterface{
    lateinit var twitter: Twitter
    lateinit var twitterDialog: Dialog
    var accToken: AccessToken? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter)
        twitter_login_btn.setOnClickListener {
            getToken()
        }
    }
    fun getTask(){

    }

    private fun getToken() {
        GlobalScope.launch(Dispatchers.Default) {
            val builder = ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey(TwitterConstants.CONSUMER_KEY)
                .setOAuthConsumerSecret(TwitterConstants.CONSUMER_SECRET)
                .setIncludeEmailEnabled(true)
            val config = builder.build()
            val factory = TwitterFactory(config)
            twitter = factory.instance
            try {
                val requestToken = twitter.oAuthRequestToken
                Log.e(TAG, "getToken: $requestToken")
                withContext(Dispatchers.Main) {
                    setupTwitterWebviewDialog(requestToken.authorizationURL)
                }
            } catch (e: Exception) {
                Log.e("// ", e.toString())
            }
        }
    }


    @JvmName("setupTwitterWebviewDialog1")
    @SuppressLint("SetJavaScriptEnabled")
    fun setupTwitterWebviewDialog(url: String) {
        twitterDialog = Dialog(this)
        val webView = WebView(this)
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = TwitterWebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
        twitterDialog.setContentView(webView)
        twitterDialog.show()
    }

    inner class TwitterWebViewClient : WebViewClient() {

        // For API 21 and above
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?,
        ): Boolean {
            if (request?.url.toString().startsWith(TwitterConstants.CALLBACK_URL)) {
                Log.d("Authorization URL: ", request?.url.toString())
                handleUrl(request?.url.toString())

                // Close the dialog after getting the oauth_verifier
                if (request?.url.toString().contains(TwitterConstants.CALLBACK_URL)) {
                    twitterDialog.dismiss()
                }
                return true
            }
            return false
        }

        // For API 19 and below
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith(TwitterConstants.CALLBACK_URL)) {
                Log.d("Authorization URL: ", url)
                handleUrl(url)

                // Close the dialog after getting the oauth_verifier
                if (url.contains(TwitterConstants.CALLBACK_URL)) {
                    twitterDialog.dismiss()
                }
                return true
            }
            return false
        }


        private fun handleUrl(url: String) {
            val uri = Uri.parse(url)
            val oauthVerifier = uri.getQueryParameter("oauth_verifier") ?: ""
            GlobalScope.launch(Dispatchers.Main) {
                accToken =
                    withContext(Dispatchers.IO) { twitter.getOAuthAccessToken(oauthVerifier) }
                getUserProfile()
            }
        }

        private suspend fun getUserProfile() {
            val usr = withContext(Dispatchers.IO) { twitter.verifyCredentials() }

            //Twitter Id
            val twitterId = usr.id.toString()
            Log.d("Twitter Id: ", twitterId)

            //Twitter Handle
            val twitterHandle = usr.screenName
            Log.d("Twitter Handle: ", twitterHandle)

            //Twitter Name
            val twitterName = usr.name
            Log.d("Twitter Name: ", twitterName)

            //Twitter Email
            val twitterEmail = usr.email
            Log.d("Twitter Email: ",
                twitterEmail
                    ?: "'Request email address from users' on the Twitter dashboard is disabled")

            // Twitter Profile Pic URL
            val twitterProfilePic = usr.profileImageURLHttps.replace("_normal", "")
            Log.d("Twitter Profile URL: ", twitterProfilePic)

            // Twitter Access Token
            Log.d("Twitter Access Token", accToken?.token ?: "")
        }
    }

    override fun onClick() {
        Log.e(TAG, "onClick: " )
    }

    override fun onClickParams(name: String) {
        Log.e(TAG, "onClickParams: $name")
    }


}