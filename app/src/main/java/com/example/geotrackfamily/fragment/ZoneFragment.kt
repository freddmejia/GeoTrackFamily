package com.example.geotrackfamily.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geotrackfamily.R
import com.example.geotrackfamily.adapter.FriendsZoneAdapter
import com.example.geotrackfamily.databinding.FriendFragmentBinding
import com.example.geotrackfamily.databinding.ZoneFragmentBinding
import com.example.geotrackfamily.models.Friend
import com.example.geotrackfamily.models.shortUser
import com.example.geotrackfamily.observer.UIObserverGeneric
import com.example.geotrackfamily.utility.CompositionObj
import com.example.geotrackfamily.viewModels.FriendViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import android.location.LocationManager
import androidx.core.app.ActivityCompat


@AndroidEntryPoint
class ZoneFragment : Fragment(R.layout.zone_fragment), UIObserverGeneric<Friend>,
    OnMapReadyCallback {
    private var binding: ZoneFragmentBinding? = null
    private val friendViewModel: FriendViewModel by viewModels()
    private lateinit var toast: Toast
    private lateinit var friendsZoneAdapter: FriendsZoneAdapter
    private var friendsList: ArrayList<Friend> = arrayListOf()
    private var tempFriendsList: ArrayList<Friend> = arrayListOf()
    private lateinit var googleMap: GoogleMap
    private var currentLocation: LatLng ? = null
    private val REQUEST_LOCATION_PERMISSION = 1
    private val TAG = "ZoneFragment"
    companion object{
        fun newInstance(): ZoneFragment {
            return ZoneFragment()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val zoneFragmentBinding = ZoneFragmentBinding.bind(view)
        binding = zoneFragmentBinding!!
        toast = Toast(this@ZoneFragment.requireContext())
        friendsList = arrayListOf()
        tempFriendsList = arrayListOf()
        friendsZoneAdapter = FriendsZoneAdapter(this@ZoneFragment.requireContext(), friendsList,this@ZoneFragment)
        binding?.rvMyFriends?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding?.rvMyFriends?.adapter = friendsZoneAdapter

        binding?.mapView?.onCreate(savedInstanceState)
        binding?.mapView?.getMapAsync(this@ZoneFragment)

        checkPermissionMap()

        events()
        coroutines()
        api()
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
                        binding?.rvPlaceHolder?.isVisible = false
                        friendsList.clear()
                        friendsList.addAll(result.data.data)
                        tempFriendsList.clear()
                        tempFriendsList.addAll(result.data.data)
                        friendsZoneAdapter.setNewData(friendsList)
                    }
                    is com.example.geotrackfamily.utility.Result.Error -> {
                        binding?.rvMyFriends?.isVisible = false
                        binding?.rvPlaceHolder?.isVisible = true
                        showToast(message = result.error)
                    }
                    else -> Unit
                }
            }
        }
    }

    fun api() {
        friendViewModel.fetch_friends()
    }


    fun showToast(message: String){
        toast?.cancel()
        toast = Toast.makeText(this@ZoneFragment.context,message, Toast.LENGTH_LONG)
        toast.show()
    }

    fun checkPermissionMap() {
        if (ActivityCompat.checkSelfPermission(this@ZoneFragment.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //ask permission
            ActivityCompat.requestPermissions(this@ZoneFragment.requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
            return
        }

        val locationManager = this@ZoneFragment.requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e(TAG, "checkPermissionMap: permission allowed ll", )
            var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            var location2 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            var currentLocationV = if (location != null) location else if (location2 != null) location2 else null

            Log.e(TAG, "checkPermissionMap: location "+location.toString() )
            if (currentLocationV != null)
                currentLocation = LatLng(location!!.latitude, location!!.longitude)
            return
        }

        showToast(message = resources.getString(R.string.error_not_enable_gps))




    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "onRequestPermissionsResult: ok", )
                // Permiso de ubicación concedido, puedes obtener la ubicación actual del dispositivo
            } else {
                Log.e(TAG, "onRequestPermissionsResult: denied", )
                // Permiso de ubicación denegado, debes informar al usuario que la funcionalidad de ubicación no estará disponible
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onOkButton(data: Friend) {

    }

    override fun onCancelButton(data: Friend) {
        Log.e("", "onCancelButton: " )
    }

    override fun onMapReady(p0: GoogleMap) {
        Log.e(TAG, "onMapReady: " )
        googleMap = p0
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isZoomControlsEnabled = true

        //if (currentLocation != null){
            Log.e(TAG, "onMapReady: 11", )
            val initialLocation = LatLng(37.7749, -122.4194)
            //val initialLocation = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            val cameraPosition = CameraPosition.Builder().target(initialLocation).zoom(10f).build()
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            googleMap.setOnMapClickListener { point ->
                draftCircleOnMap(
                    latitude = point.latitude,
                    longitude = point.longitude
                )
            }
            return
        //}

        //Log.e(TAG, "onMapReady: 22", )
        //checkPermissionMap()


    }

    fun draftCircleOnMap(latitude: Double, longitude: Double) {
        val radiusInMeters = 400
        val center = LatLng(latitude, longitude)
        val radius = radiusInMeters.toDouble()
        val circleOptions = CircleOptions()
            .center(center)
            .radius(radius)
            .strokeColor(Color.RED)
            .fillColor(Color.argb(70, 255, 0, 0))
        val circle = googleMap.addCircle(circleOptions)
    }
}