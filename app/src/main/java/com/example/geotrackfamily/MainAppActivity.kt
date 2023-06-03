package com.example.geotrackfamily

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.geotrackfamily.databinding.ActivityMainAppBinding
import com.example.geotrackfamily.databinding.BottomBarBinding
import com.example.geotrackfamily.databinding.ToolbarAppBinding
import com.example.geotrackfamily.fragment.FriendFragment
import com.example.geotrackfamily.fragment.HomeFragment
import com.example.geotrackfamily.fragment.ZoneFragment
import com.example.geotrackfamily.models.User
import com.example.geotrackfamily.utility.LocationService
import com.example.geotrackfamily.utility.Utils
import com.example.geotrackfamily.viewModels.UserViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import android.provider.Settings
import android.view.KeyEvent
import android.view.ViewConfiguration
import androidx.core.app.NotificationManagerCompat

@AndroidEntryPoint
class MainAppActivity : AppCompatActivity() {
    val TAG = "MainAppActivity"
    private lateinit var binding: ActivityMainAppBinding
    private lateinit var toolbarAppBinding: ToolbarAppBinding
    private lateinit var bottomBarBinding: BottomBarBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var user: User
    private lateinit var sharedPref: SharedPreferences
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var toast: Toast
    private var volumeDownPressTime: Long = 0
    private val requiredPressCount = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbarAppBinding = ToolbarAppBinding.bind(binding.root)
        bottomBarBinding = BottomBarBinding.bind(binding.root)
        setSupportActionBar(toolbarAppBinding.toolbar)


        chooseSelectionMenu(fragment = HomeFragment.newInstance())

        toast = Toast(this)

        sharedPref = this@MainAppActivity.getSharedPreferences(
            getString(R.string.shared_preferences), Context.MODE_PRIVATE)

        try {
            user = User(JSONObject(sharedPref!!.getString("user","")))
            getTokenFirebase(user = user)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }


        askPermissions()
        events()
    }

    fun askPermissions() {

        if ((ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED)) {
            Utils.permissionLocation(this@MainAppActivity, this@MainAppActivity)
        }
        else{
            initServiceLocation()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                // Notifications are disabled, request the user to enable them
                permissionNotification()
            }
        }
    }

    override fun onBackPressed() {
        try {
            val currentFragment: Fragment? =
                supportFragmentManager.fragments.last()
            Log.e(TAG, "onResume: "+currentFragment?.toString() )

        }catch (e: java.lang.Exception){
            Log.e(TAG, "Exception: "+e.message )
        }
        super.onBackPressed()

    }

    fun events() {
        toolbarAppBinding.profile.setOnClickListener {
            startActivity(
                Intent(this@MainAppActivity, ProfileActivity::class.java)
            )
        }
        toolbarAppBinding.rvNotification.setOnClickListener {
            startActivity(
                Intent(this@MainAppActivity, NotificationUserActivity::class.java)
            )
        }

        bottomBarBinding.rvHome.setOnClickListener {
            chooseSelectionMenu(fragment = HomeFragment.newInstance())
        }
        bottomBarBinding.rvFriend.setOnClickListener {
            chooseSelectionMenu(fragment = FriendFragment.newInstance())
        }
        bottomBarBinding.rvZone.setOnClickListener {
            chooseSelectionMenu(fragment = ZoneFragment.newInstance())
        }

    }

    fun chooseSelectionMenu(fragment: Fragment){
        when(fragment){
            is HomeFragment -> {
                appeareance(
                    cardView = bottomBarBinding.cvHome,
                    imageView = bottomBarBinding.imHome,
                    colorCv = R.color.green_b1,
                    colorIm = R.color.white
                )

                appeareance(
                    cardView = bottomBarBinding.cvFriend,
                    imageView = bottomBarBinding.imFriend,
                    colorCv = R.color.white,
                    colorIm = R.color.black
                )

                appeareance(
                    cardView = bottomBarBinding.cvZone,
                    imageView = bottomBarBinding.imZone,
                    colorCv = R.color.white,
                    colorIm = R.color.black
                )
                toolbarAppBinding.titleBar.setText(resources.getString(R.string.my_friends))
            }
            is FriendFragment -> {
                appeareance(
                    cardView = bottomBarBinding.cvFriend,
                    imageView = bottomBarBinding.imFriend,
                    colorCv = R.color.green_b1,
                    colorIm = R.color.white
                )

                appeareance(
                    cardView = bottomBarBinding.cvHome,
                    imageView = bottomBarBinding.imHome,
                    colorCv = R.color.white,
                    colorIm = R.color.black
                )

                appeareance(
                    cardView = bottomBarBinding.cvZone,
                    imageView = bottomBarBinding.imZone,
                    colorCv = R.color.white,
                    colorIm = R.color.black
                )
                toolbarAppBinding.titleBar.setText(resources.getString(R.string.track_friend))
            }
            is ZoneFragment -> {
                appeareance(
                    cardView = bottomBarBinding.cvZone,
                    imageView = bottomBarBinding.imZone,
                    colorCv = R.color.green_b1,
                    colorIm = R.color.white
                )

                appeareance(
                    cardView = bottomBarBinding.cvHome,
                    imageView = bottomBarBinding.imHome,
                    colorCv = R.color.white,
                    colorIm = R.color.black
                )

                appeareance(
                    cardView = bottomBarBinding.cvFriend,
                    imageView = bottomBarBinding.imFriend,
                    colorCv = R.color.white,
                    colorIm = R.color.black
                )
                toolbarAppBinding.titleBar.setText(resources.getString(R.string.geozone_friend))
            }
        }
        supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out)
            replace(R.id.frameContainer,fragment)
            setReorderingAllowed(true)
        }
    }

    fun appeareance(cardView: CardView, imageView: ImageView, colorCv: Int, colorIm: Int){
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, colorCv))
        imageView.setColorFilter(ContextCompat.getColor(this, colorIm), PorterDuff.Mode.SRC_IN)
    }


    private fun isServiceNotRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainAppActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        initServiceLocation()
                    }
                } else {
                    Toast.makeText(this,resources.getString(R.string.ask_permission_location), Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    fun initServiceLocation() {
        try {
            val intent = Intent(this, LocationService::class.java)
            if (isServiceNotRunning(LocationService::class.java)) {
                if (!getLocationControlByUser() || getLocationRunning()){
                    startService(intent)
                    isLocationRunning(isLocationRunning = true)
                }
            }
        }catch (e: java.lang.Exception){
            isLocationRunning(isLocationRunning = false)
        }
    }

    fun getTokenFirebase(user: User) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            userViewModel.update_token(user_id = user.id.toString(), token = token)
        })
    }

    fun isLocationRunning(isLocationRunning: Boolean) {
        with(sharedPref?.edit()){
            this?.putBoolean("isLocationRunning",isLocationRunning)
            this?.apply()
        }
    }
    fun getLocationRunning() = sharedPref!!.getBoolean("isLocationRunning",false)
    //this function works the first time to get location, because the user cant click on button to control that
    fun getLocationControlByUser() = sharedPref!!.getBoolean("isControlByUser",false)

    fun permissionNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            // Check if the user granted notification permission after the request
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                    // Notification permission granted
                    // Continue with your app logic here
                } else {
                    showToast(message = resources.getString(R.string.ask_permission_notification))
                    // Notification permission not granted
                    // You can show a message to the user or take any additional action if desired
                }
            }
        }
    }

    fun showToast(message: String){
        toast?.cancel()
        toast = Toast.makeText(this@MainAppActivity,message, Toast.LENGTH_LONG)
        toast.show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - volumeDownPressTime <= ViewConfiguration.getDoubleTapTimeout() * requiredPressCount) {
                // Se han presionado tres veces seguidas el botón de volumen hacia abajo
                sendPanicAlert()
            }
            volumeDownPressTime = currentTime
        }
        return super.onKeyDown(keyCode, event)
    }
    fun sendPanicAlert() {
        Log.e(TAG, "sendPanicAlert: ", )
        userViewModel.panicAlert(user_id = user.id.toString())
    }
    /*  override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        try {


            if (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - volumeDownPressTime <= ViewConfiguration.getDoubleTapTimeout() * requiredPressCount) {

                    //if (currentTime - volumeDownPressTime <= ViewConfiguration.getDoubleTapTimeout()) {
                    // Se han presionado dos veces seguidas el botón de volumen hacia abajo
                    // Realizar la acción de pánico aquí
                    sendPanicAlert()
                }
                volumeDownPressTime = currentTime
            }
        }
        catch (e: java.lang.Exception){

        }
        return super.dispatchKeyEvent(event)
    }

    fun sendPanicAlert() {
        Log.e(TAG, "sendPanicAlert: ", )
        userViewModel.panicAlert(user_id = user.id.toString())
    }*/
}