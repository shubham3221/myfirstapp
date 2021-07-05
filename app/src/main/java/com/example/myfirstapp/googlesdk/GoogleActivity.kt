package com.example.myfirstapp.googlesdk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myfirstapp.LocaleUtils
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.example.myfirstapp.twitterLogin.TwitterActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_google.*


class GoogleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google)

        val locale = LocaleUtils().setLocale(this, "es");
        val resources = locale!!.getResources();
        req_permisson.setText(resources.getString(R.string.first_name));


        //first step
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(this, gso)


        optionalWork()


        //optional step
        //changing google signin button size
        // Set the dimensions of the sign-in button.
        // Set the dimensions of the sign-in button.
        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)


        //third step
        val registerForActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    Log.e(TAG, "onCreate: callback")
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(it.data)
                    handleSignInResult(task)
                }
            }

        signInButton.setOnClickListener {
            registerForActivityResult.launch(client.signInIntent)
        }


    }

    @SuppressLint("NewApi")
    private fun optionalWork() {
        //storage permission
        val requestPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    Log.e("//", "Storage permnission granted")
                } else {
                    Log.e("//", "Storage permnission denied")
                }
            }

        req_permisson.setOnClickListener {
            requestPermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        //image
        val imagesCallback =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                Log.e(TAG, "optionalWork : ")
                it?.let {
                    Log.e(TAG, "optionalWork : $it")
                }
            }
        //nextScreen
        val nextActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                it?.let {
                    Log.e(TAG, "optionalWork: ${it.toString()}")
                }
            }

        gotonext.setOnClickListener {
            val intent = Intent(this, TwitterActivity::class.java)
            intent.putExtra("myresult",10)
            nextActivity.launch(intent)
//            imagesCallback.launch(multiple_Image_selection())
        }
    }

    private fun multiple_Image_selection() :Intent{
        var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        return intent
    }

    fun single_Image_selection() :Intent{
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            Log.e(TAG, "handleSignInResult: " + account!!.displayName)
            Log.e(TAG, "handleSignInResult: " + account!!.email)
            Log.e(TAG, "handleSignInResult: " + account!!.givenName)
            Log.e(TAG, "handleSignInResult: " + account!!.id)
            // Signed in successfully, show authenticated UI.
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
//        val account = GoogleSignIn.getLastSignedInAccount(this)
//        Log.e(TAG, "onStart: " + account.toString())
//        updateUI(account)

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
//        val googleSignInClient = GoogleSignIn.getClient(this, gso)
//        googleSignInClient.signOut()
    }
}