package com.example.geotrackfamily

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.example.geotrackfamily.databinding.ActivityGeofenceFriendBinding
import com.example.geotrackfamily.databinding.ActivityProfileBinding
import com.example.geotrackfamily.databinding.ToolbarAppBinding
import com.example.geotrackfamily.models.Friend
import com.example.geotrackfamily.models.User
import com.example.geotrackfamily.viewModels.FriendViewModel
import com.example.geotrackfamily.viewModels.UserViewModel
import org.json.JSONObject

class GeofenceFriendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGeofenceFriendBinding
    private val friendViewModel: FriendViewModel by viewModels()
    private lateinit var toast: Toast
    private lateinit var toolbarAppBinding: ToolbarAppBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeofenceFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolBar()

        toast = Toast(this)
        events()
        coroutines()
    }
    fun events() {

    }

    fun coroutines() {

    }

    fun setUpToolBar() {
        toolbarAppBinding = ToolbarAppBinding.bind(binding.root)
        setSupportActionBar(toolbarAppBinding.toolbar)
        toolbarAppBinding.titleBar.text = resources.getString(R.string.geofences_friend)
        toolbarAppBinding.profile.isVisible = false
        toolbarAppBinding.rvNotification.isVisible = false
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->
            {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}