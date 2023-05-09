package com.example.geotrackfamily

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.geotrackfamily.adapter.FriendGeofenceAdapter
import com.example.geotrackfamily.databinding.ActivityGeofenceFriendBinding
import com.example.geotrackfamily.databinding.ActivityProfileBinding
import com.example.geotrackfamily.databinding.ToolbarAppBinding
import com.example.geotrackfamily.models.Friend
import com.example.geotrackfamily.models.GeofenceFriend
import com.example.geotrackfamily.models.User
import com.example.geotrackfamily.observer.UIObserverGeofenceD
import com.example.geotrackfamily.utility.CompositionObj
import com.example.geotrackfamily.utility.Utils
import com.example.geotrackfamily.viewModels.FriendViewModel
import com.example.geotrackfamily.viewModels.UserViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.lang.reflect.Type
@AndroidEntryPoint
class GeofenceFriendActivity : AppCompatActivity(), UIObserverGeofenceD {
    private lateinit var binding: ActivityGeofenceFriendBinding
    private val friendViewModel: FriendViewModel by viewModels()
    private lateinit var toast: Toast
    private lateinit var toolbarAppBinding: ToolbarAppBinding
    private lateinit var user: User
    private lateinit var friend: Friend
    private lateinit var adapter: FriendGeofenceAdapter
    private var geofences = arrayListOf<GeofenceFriend>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeofenceFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolBar()

        toast = Toast(this)
        val sharedPref = this@GeofenceFriendActivity.getSharedPreferences(
            getString(R.string.shared_preferences), Context.MODE_PRIVATE)

        user = User(JSONObject(sharedPref!!.getString("user","")))
        geofences = arrayListOf()
        adapter = FriendGeofenceAdapter(context = this, list =  geofences, this)
        binding.rvGeofences.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        binding.rvGeofences.adapter = adapter

        api(intent.extras!!)

        events()
        coroutines()
    }
    fun events() {

    }

    fun coroutines() {
        lifecycleScope.launchWhenCreated {
            friendViewModel.loadingProgress.collect {
                binding.linear.isVisible = !it
                binding.progressBar.isVisible = it
            }
        }

        lifecycleScope.launchWhenCreated {
            friendViewModel.compositionGeofencesFriends.collect { result ->
                when(result) {
                    is com.example.geotrackfamily.utility.Result.Success<CompositionObj<java.util.ArrayList<GeofenceFriend>, String>> -> {
                        geofences.clear()
                        geofences.addAll(result.data.data)
                        adapter.setNewData(geofences)
                    }
                    is com.example.geotrackfamily.utility.Result.Error -> {
                        finish()
                        showToast(message = result.error)
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            friendViewModel.compositionGeofenceFriend.collect { result ->
                when(result) {
                    is com.example.geotrackfamily.utility.Result.Success<CompositionObj<GeofenceFriend, String>> -> {
                        Log.e("", "coroutines: del "+result.data.data.id.toString() )
                        deleteGeofence(id = result.data.data.id)

                        showToast(message = result.data.message)
                    }
                    is com.example.geotrackfamily.utility.Result.Error -> {
                        showToast(message = result.error)
                    }
                    else -> Unit
                }

            }
        }

    }

    fun api(bundle: Bundle) {
        if (bundle != null) {
            val brandType: Type = object : TypeToken<Friend>() {}.type
            friend = Gson().fromJson(bundle.getString("friend"),brandType)
            binding.tvNameUser.text = friend.name
            friendViewModel.fetch_geofence_byfriend(
                user_id1 = user.id.toString(),
                user_id2 = friend.id.toString()
            )
        }
    }

    fun showToast(message: String){
        toast?.cancel()
        toast = Toast.makeText(this@GeofenceFriendActivity,message, Toast.LENGTH_LONG)
        toast.show()
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

    override fun deleteGeofence(geofence: GeofenceFriend) {
        friendViewModel.delete_geofence_byfriend(geofenceId = geofence.id.toString())
    }

    fun deleteGeofence(id: Int) {
        val geofen = geofences.filter { it.id == id }.singleOrNull()
        if (geofen != null) {
            geofences.remove(geofen)
            adapter.setNewData(geofences)
        }

        if (geofences.size <= 0){
            finish()
            showToast(message = resources.getString(R.string.error_no_data))
        }
    }
}