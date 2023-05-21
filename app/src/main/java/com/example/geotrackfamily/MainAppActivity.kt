package com.example.geotrackfamily

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.geotrackfamily.databinding.ActivityMainAppBinding
import com.example.geotrackfamily.databinding.BottomBarBinding
import com.example.geotrackfamily.databinding.ToolbarAppBinding
import com.example.geotrackfamily.fragment.FriendFragment
import com.example.geotrackfamily.fragment.HomeFragment
import com.example.geotrackfamily.fragment.ZoneFragment
import com.example.geotrackfamily.utility.LocationService
import com.example.geotrackfamily.utility.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainAppActivity : AppCompatActivity() {
    val TAG = "MainAppActivity"
    private lateinit var binding: ActivityMainAppBinding
    private lateinit var toolbarAppBinding: ToolbarAppBinding
    private lateinit var bottomBarBinding: BottomBarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbarAppBinding = ToolbarAppBinding.bind(binding.root)
        bottomBarBinding = BottomBarBinding.bind(binding.root)
        setSupportActionBar(toolbarAppBinding.toolbar)

        events()
        chooseSelectionMenu(fragment = HomeFragment.newInstance())

        if ((ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED)) {
            Utils.permissionLocation(this@MainAppActivity, this@MainAppActivity)
        }
        else{
            initServiceLocation()
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
                //bottomBarBinding.cvHome.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green_b1))
                //bottomBarBinding.imHome.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN)

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
                //bottomBarBinding.cvZone.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green_b1))
                //bottomBarBinding.imZone.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN)

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
                startService(intent)
            }
        }catch (e: java.lang.Exception){
            Log.e(TAG, "initServiceLocation: "+e.message.toString() )
        }
    }

}