package com.example.geotrackfamily.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
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
import androidx.core.graphics.drawable.DrawableCompat

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
        binding?.etUsername?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputText = s.toString()
                if (inputText.trim().length >= 1){
                    val tmp = tempFriendsList.filter { it.name.toLowerCase().contains(inputText.toLowerCase())  }
                    if (tmp.isNotEmpty())
                        friendsZoneAdapter.setNewData(tmp)
                }
                if (inputText.trim().length <= 0)
                    friendsZoneAdapter.setNewData(friendsList)

            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
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
                        showUbicationFriend(latitude = latitude, longitude = longitude, friend = friendCh)
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
                        showUbicationFriend(latitude = latitude, longitude = longitude, friend = friendCh)
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

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun showUbicationFriend (latitude: Double, longitude: Double, friend: Friend) {
        activity?.runOnUiThread {
            googleMap?.clear()
            val latLng = LatLng(latitude, longitude)

            // Crear la vista personalizada del marcador
            val markerView = LayoutInflater.from(this@FriendFragment.requireContext()).inflate(R.layout.location_friend_item, null)
            val markerTextView = markerView.findViewById<TextView>(R.id.markerTextView)

            // Configurar el texto adicional
            markerTextView.text = friend.name

            // Medir y ajustar el tamaño de la vista del marcador
            markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)

            // Crear el bitmap del marcador personalizado
            val bitmap = Bitmap.createBitmap(markerView.measuredWidth, markerView.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            markerView.draw(canvas)

            val markerOptions = MarkerOptions()
                .position(latLng) // LatLng de la ubicación
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)) // Ícono personalizado
                .title(friend.name)
                .anchor(0.5f, 0.5f) // Ajustar el anclaje del icono al centro del marcador
                .infoWindowAnchor(0.5f, 0.5f) // Ajustar el anclaje de la ventana de información al centro del marcador

            googleMap?.addMarker(markerOptions)
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            googleMap?.uiSettings?.isZoomGesturesEnabled = true
        }
    }

}