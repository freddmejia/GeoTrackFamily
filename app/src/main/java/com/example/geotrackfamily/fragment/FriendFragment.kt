package com.example.geotrackfamily.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geotrackfamily.MainActivity
import com.example.geotrackfamily.R
import com.example.geotrackfamily.adapter.FriendsZoneAdapter
import com.example.geotrackfamily.databinding.FriendFragmentBinding
import com.example.geotrackfamily.databinding.HomeFragmentBinding
import com.example.geotrackfamily.models.Friend
import com.example.geotrackfamily.models.LocationUser
import com.example.geotrackfamily.models.User
import com.example.geotrackfamily.observer.UIObserverGeneric
import com.example.geotrackfamily.utility.CompositionObj
import com.example.geotrackfamily.utility.Utils
import com.example.geotrackfamily.viewModels.FriendViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.*
import okio.ByteString
import org.json.JSONObject

@AndroidEntryPoint
class FriendFragment : Fragment(R.layout.friend_fragment) , UIObserverGeneric<Friend>,
    OnMapReadyCallback {
    private var binding: FriendFragmentBinding? = null
    private lateinit var friendsZoneAdapter: FriendsZoneAdapter
    private var friendsList: ArrayList<Friend> = arrayListOf()
    private var tempFriendsList: ArrayList<Friend> = arrayListOf()
    private lateinit var googleMap: GoogleMap
    private val friendViewModel: FriendViewModel by viewModels()
    private lateinit var webSocket: WebSocket
    private lateinit var client: OkHttpClient
    private lateinit var friendCh: Friend
    private lateinit var toast: Toast
    companion object{
        fun newInstance(): FriendFragment {
            return FriendFragment()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val friendFragmenBinding = FriendFragmentBinding.bind(view)
        binding = friendFragmenBinding!!

        toast = Toast(this@FriendFragment.requireContext())
        friendsList = arrayListOf()
        tempFriendsList = arrayListOf()
        friendsZoneAdapter = FriendsZoneAdapter(this@FriendFragment.requireContext(), friendsList,this@FriendFragment)
        binding?.rvMyFriends?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding?.rvMyFriends?.adapter = friendsZoneAdapter


        binding?.mapView?.onCreate(savedInstanceState)
        binding?.mapView?.onResume()
        binding?.mapView?.getMapAsync(this)

        client = OkHttpClient()
        friendCh = Friend()
        events()
        coroutines()
        api()

    }
    fun api() {
        friendViewModel.fetch_friends()
    }


    fun events(){

    }

    fun coroutines() {
        lifecycleScope.launchWhenCreated {
            friendViewModel.loadingProgress.collect {
                binding?.linear?.isVisible = !it
                binding?.progressBar?.isVisible = it
            }
        }
        lifecycleScope.launchWhenCreated {
            friendViewModel.compositionFetchPossibleFriends.collect { result->
                when(result) {
                    is com.example.geotrackfamily.utility.Result.Success<CompositionObj<ArrayList<Friend>, String>> -> {
                        binding?.rvMyFriends?.isVisible = true
                        binding?.mapView?.isVisible = true
                        binding?.rvPlaceHolder?.isVisible = false

                        friendsList.clear()
                        friendsList.addAll(result.data.data)
                        tempFriendsList.clear()
                        tempFriendsList.addAll(result.data.data)
                        friendsZoneAdapter.setNewData(friendsList)
                        getLocationUser()
                    }
                    is com.example.geotrackfamily.utility.Result.Error -> {
                        binding?.rvMyFriends?.isVisible = false
                        binding?.mapView?.isVisible = false
                        binding?.rvPlaceHolder?.isVisible = true
                    }
                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            friendViewModel.compositionLocationFriend.collect { result->
                when(result) {
                    is com.example.geotrackfamily.utility.Result.Success<CompositionObj<LocationUser,String>> -> {
                        val latitude = result.data.data.latitude.toDouble()
                        val longitude = result.data.data.longitude.toDouble()
                        showUbicationFriend(latitude = latitude, longitude = longitude)
                    }
                    is com.example.geotrackfamily.utility.Result.Error-> {
                        showToast(message = result.error)
                    }
                    else -> Unit
                }

            }
        }


    }


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onOkButton(data: Friend) {
        friendCh = data
        friendViewModel.fetch_last_locationUser(user_id = data.id.toString())
        Log.e("", "onOkButton: "+data.id.toString())
        var jsn = JSONObject()
        jsn.put("user_id",friendCh.id.toString())
        Log.e("", "getLocationUser JSON: "+jsn.toString() )
        webSocket?.send(jsn.toString())

    }

    override fun onCancelButton(data: Friend) {

    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        googleMap?.uiSettings?.isScrollGesturesEnabled = true
        googleMap?.uiSettings?.isZoomGesturesEnabled = true
        googleMap?.uiSettings?.isRotateGesturesEnabled = true
        googleMap?.uiSettings?.isTiltGesturesEnabled = false

    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding?.mapView?.onSaveInstanceState(outState)
    }

    fun getLocationUser() {
        //webSocket?.cancel()

        val request = Request.Builder()
            .url(Utils.urlSocket)
            .build()
        webSocket = client.newWebSocket(request, object : WebSocketListener(){
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.e("", "onOpen: websocket", )
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                try {

                    val locationJson = JSONObject(text)
                    Log.e("", "friendCh "+friendCh.id.toString()+" "+"onMessage: gg "+ locationJson.getString("latitude"))
                    if (friendCh.id > 0 && friendCh.id == locationJson.getInt("user_id")) {
                        val latitude = locationJson.getString("latitude").toDouble()
                        val longitude = locationJson.getString("longitude").toDouble()
                        showUbicationFriend(latitude = latitude, longitude = longitude)
                    }

                }catch (e: java.lang.Exception){
                    e.printStackTrace()
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                Log.e("", "onMessage: "+bytes.toString() )
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Log.e("", "onClosed: ", )
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.e("", "onFailure: ", )
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            webSocket?.cancel()
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }

    }

    fun showToast(message: String){
        toast?.cancel()
        toast = Toast.makeText(this@FriendFragment.context,message, Toast.LENGTH_LONG)
        toast.show()
    }

    fun showUbicationFriend (latitude: Double, longitude: Double) {
        activity?.runOnUiThread {
            googleMap?.clear()
            val latLng = LatLng(latitude, longitude)
            val markerOptions = MarkerOptions()
                .position(latLng) // LatLng de la ubicación
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ubication_icon)) // Ícono personalizado
                .title("Ubication")

            googleMap?.addMarker(markerOptions) // Agreg
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            googleMap?.uiSettings?.isZoomGesturesEnabled = true

        }
    }
}