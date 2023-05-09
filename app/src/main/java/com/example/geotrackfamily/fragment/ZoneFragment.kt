package com.example.geotrackfamily.fragment

import android.Manifest

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.geotrackfamily.adapter.FriendsZoneAdapter
import com.example.geotrackfamily.databinding.ZoneFragmentBinding
import com.example.geotrackfamily.models.Friend
import com.example.geotrackfamily.observer.UIObserverGeneric
import com.example.geotrackfamily.utility.CompositionObj
import com.example.geotrackfamily.viewModels.FriendViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList
import com.example.geotrackfamily.R
import com.example.geotrackfamily.databinding.ToolbarAppBinding
import com.example.geotrackfamily.dialogs.GeoDialogs
import com.example.geotrackfamily.models.GeofenceFriend
import com.example.geotrackfamily.models.User
import com.example.geotrackfamily.observer.UIObserverFriendGeoZone
import com.example.geotrackfamily.observer.UIObserverGeofenceD
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import org.json.JSONObject

@AndroidEntryPoint
class ZoneFragment : Fragment(R.layout.zone_fragment), UIObserverGeneric<Friend>,
    OnMapReadyCallback, UIObserverFriendGeoZone, UIObserverGeofenceD {
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
    private var placesClient: PlacesClient ? = null
    private var friendChoosed : Friend? = null
    private var latitudeFriend : String = ""
    private var longitudeFriend : String = ""
    private var zoneFriend : String = ""
    private var ratioFriend : String = ""
    private lateinit var user: User
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
        ratioFriend = "400"

        val sharedPref = this@ZoneFragment.requireContext().getSharedPreferences(
            getString(R.string.shared_preferences), Context.MODE_PRIVATE)

        user = User(JSONObject(sharedPref!!.getString("user","")))

        Places.initialize(this@ZoneFragment.requireContext(), "AIzaSyC1GP9e25BZ7ga1aYSisdpLvuoopiGkoQ4");
        placesClient = Places.createClient(this@ZoneFragment.requireContext())

        binding?.mapView?.onCreate(savedInstanceState)
        binding?.mapView?.onResume()
        binding?.mapView?.getMapAsync(this)

        checkPermissionMap()
        placesGoogleMapsApi()
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
        lifecycleScope.launchWhenCreated {
            friendViewModel.compositionGeofenceFriend.collect { result ->
                when(result) {
                    is com.example.geotrackfamily.utility.Result.Success<CompositionObj<GeofenceFriend, String>> -> {
                        showToast(message = result.data.message)
                    }
                    is com.example.geotrackfamily.utility.Result.Error -> {
                        showToast(message = result.error)
                    }
                    else -> Unit
                }

            }
        }
        lifecycleScope.launchWhenCreated {
            friendViewModel.compositionGeofencesFriends.collect { result ->
                when(result) {
                    is com.example.geotrackfamily.utility.Result.Success<CompositionObj<java.util.ArrayList<GeofenceFriend>, String>> -> {
                        GeoDialogs.friends_geozone_delete_dialog(
                            geozeones =  result.data.data,
                            context = this@ZoneFragment.requireContext(),
                            observer  = this@ZoneFragment)
                    }
                    is com.example.geotrackfamily.utility.Result.Error -> {
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
            Log.e(TAG, "checkPermissionMap: permission allowed ll")
            var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            var location2 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            var currentLocationV = if (location != null) location else  location2

            Log.e(TAG, "checkPermissionMap: location "+currentLocationV.toString() +"\n"+
                currentLocationV?.latitude.toString() + " "+currentLocationV?.longitude.toString()
            )
            if (currentLocationV != null) {
                currentLocation = LatLng(currentLocationV!!.latitude, currentLocationV!!.longitude)
                return
            }
        }

        showToast(message = resources.getString(R.string.error_not_enable_gps))




    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "onRequestPermissionsResult: ok")
                // Permiso de ubicación concedido, puedes obtener la ubicación actual del dispositivo
            } else {
                Log.e(TAG, "onRequestPermissionsResult: denied")
                // Permiso de ubicación denegado, debes informar al usuario que la funcionalidad de ubicación no estará disponible
            }
        }
    }

    fun placesGoogleMapsApi() {

        val placesSearchFragment = this@ZoneFragment.childFragmentManager.findFragmentById(R.id.places_search_fragment) as AutocompleteSupportFragment

        placesSearchFragment.setPlaceFields(
            Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )
        placesSearchFragment.setTypeFilter(TypeFilter.ADDRESS)
        //placesSearchFragment.setCountry("MX") // Opcional: establece el país para restringir los resultados de búsqueda a un país específico
        placesSearchFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.e(TAG, "onPlaceSelected: place "+place.name )
                zoneFriend = place.name
                latitudeFriend = ""
                longitudeFriend = ""
                val latLng = place.latLng
                val markerOptions = MarkerOptions()
                    .position(latLng!!)
                    .title(place.name)
                googleMap.addMarker(markerOptions)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
            }

            override fun onError(status: Status) {
                Log.e(TAG, "Error al buscar lugar: $status")
            }
        })

    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onOkButton(data: Friend) {
        Log.e(TAG, "onOkButton: ", )
        var isChoosed = false
        if (latitudeFriend.isNotEmpty() && longitudeFriend.isNotEmpty()) {
            friendChoosed = data
            isChoosed = true
        }
        if (isChoosed) {
            friendsList.forEach {
                //it.is_choosed = it.id == friendChoosed?.id
            }
            friendsZoneAdapter.setNewData(friendsList)

            GeoDialogs.friends_geozone_dialog(
                friend = friendChoosed!!,
                context = this@ZoneFragment.requireContext(),
                observer = this
            )
        }




    }

    override fun onCancelButton(data: Friend) {
        Log.e("", "onCancelButton: " )
        friendViewModel.fetch_geofence_byfriend(
            user_id1 = user.id.toString(),
            user_id2 = data.id.toString()
        )
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


        if (currentLocation != null){
            Log.e(TAG, "onMapReady: 1111")
            //val initialLocation = LatLng(37.7749, -122.4194)
            val initialLocation = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            val cameraPosition = CameraPosition.Builder().target(initialLocation).zoom(10f).build()
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            googleMap.setOnMapClickListener { point ->
                Log.e(TAG, "onMapReady setOnMapClickListener: ", )
                draftCircleOnMap(
                    latitude = point.latitude,
                    longitude = point.longitude
                )
            }
            return
        }

        //Log.e(TAG, "onMapReady: 22", )
        //checkPermissionMap()


    }

    fun draftCircleOnMap(latitude: Double, longitude: Double) {
        latitudeFriend = latitude.toString()
        longitudeFriend = longitude.toString()
        val radiusInMeters = 400
        val center = LatLng(latitude, longitude)
        val radius = radiusInMeters.toDouble()
        val circleOptions = CircleOptions()
            .center(center)
            .radius(radius)
            .strokeColor(Color.RED)
            .fillColor(Color.argb(70, 255, 0, 0))
        googleMap.clear()
        val circle = googleMap.addCircle(circleOptions)
    }

    override fun onResume() {
        super.onResume()
        binding?.mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding?.mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding?.mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding?.mapView?.onSaveInstanceState(outState)
    }

    override fun addedGeo() {
        friendViewModel.saveFriendGeofence(
            user_id1 = user.id.toString(),
            user_id2 = friendChoosed?.id.toString(),
            longitude = longitudeFriend,
            latitude = latitudeFriend,
            ratio = ratioFriend,
            zone = zoneFriend
        )
    }

    override fun deleteGeofence(geofence: GeofenceFriend) {
        friendViewModel.delete_geofence_byfriend(geofenceId = geofence.id.toString())
    }

}