package com.example.myfirstapp.googlesdk

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.example.myfirstapp.linkedin.Loginactivity
import com.example.myfirstapp.notifications.NotificationActivity
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
        checkboxPermission()
        addAutoStartup()

        //first step
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(this, gso)

        optionalWork()

        //optional step
        //changing google signin button size
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
    private fun addAutoStartup() {
        try {
            val intent = Intent()
            val manufacturer = Build.MANUFACTURER
            Log.e(TAG, "addAutoStartup: "+manufacturer )
            if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName("com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity")
            } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName("com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity")
            } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName("com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")
            } else if ("Letv".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName("com.letv.android.letvsafe",
                    "com.letv.android.letvsafe.AutobootManageActivity")
            } else if ("Honor".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName("com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity")
            }
            val list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (list.size > 0) {
                Log.e(TAG, "success: " )
                startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e("//", e.toString())
        }
    }

    private fun checkboxPermission() {
        val permissionCheck1 = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionCheck2 = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION)
        val permissionCheck3 = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissionCheck1 and permissionCheck2 and permissionCheck3 == 0) {
            checkbox.isChecked = true
        }
    }


    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", this!!.packageName, null)
        intent.data = uri
//        startActivityForResult(intent, REQUEST_SETTINGS)
        startActivity(intent);
    }


    // show setting dialog for Android 11 version
    private fun showSettingsDialog() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Need Permission")
        builder.setMessage("Please Allow all required permission")
        builder.setPositiveButton("Go to setting") { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setCancelable(false)
        builder.show()

    }

    @SuppressLint("NewApi")
    private fun optionalWork() {
        //storage permission
        val requestPermission =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                Log.e(TAG, "optionalWork: " + it)
                if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true && it[Manifest.permission.ACCESS_COARSE_LOCATION] == true){
                    Log.e(TAG, "optionalWork: all permission granted")
                    checkboxPermission()
                }else{
                    showSettingsDialog()
                }
            }


        //demo
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        }

        req_permisson.setOnClickListener {
            requestPermission.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
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
                val let = it.data?.let { data ->
                    val extras = data.extras!!.get("activity")
                    extras
                }
                it?.let {
                    Log.e(TAG, "optionalWork: ${let}")
                }
            }

        gotonext.setOnClickListener {
            val intent = Intent(this, TwitterActivity::class.java)
            intent.putExtra("myresult", 10)
            nextActivity.launch(intent)
//            imagesCallback.launch(multiple_Image_selection())
        }
        gotonext2.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            intent.putExtra("myresult", 10)
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
//        val account = GoogleSignIn.getLastSignedInAccount(this)
//        Log.e(TAG, "onStart: " + account.toString())
//        updateUI(account)

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
//        val googleSignInClient = GoogleSignIn.getClient(this, gso)
//        googleSignInClient.signOut()
    }
}