package com.example.geotrackfamily

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.geotrackfamily.databinding.ActivitySplashGeoBinding

class SplashGeoActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashGeoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashGeoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({

            isUserLoggued()
        }, 2000)
    }

    fun isUserLoggued(){
        val prefsUser = this@SplashGeoActivity?.getSharedPreferences(
            resources.getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )
        val islogged = prefsUser!!.getBoolean("islogged",false)
        if (!islogged){
            startActivity(
                Intent(this@SplashGeoActivity, MainActivity::class.java)
            )
            finish()
            return
        }
        startActivity(
            Intent(this@SplashGeoActivity, MainAppActivity::class.java)
        )
        finish()
    }
}