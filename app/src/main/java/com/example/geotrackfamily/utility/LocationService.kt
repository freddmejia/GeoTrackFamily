package com.example.geotrackfamily.utility


import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.geotrackfamily.R
import com.example.geotrackfamily.datasources.UserRemoteDataSource
import com.example.geotrackfamily.interfaces.UserServiceRemote
import com.example.geotrackfamily.models.User
import com.example.geotrackfamily.repository.UserRepository
import com.example.geotrackfamily.viewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.CustomInjection.inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {
    private lateinit var locationManager: LocationManager
    @Inject
    lateinit var userRepository: UserRepository


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Configurar la notificación del servicio en primer plano
        val channelId = "LocationServiceChannel"
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Location Service")
            .setContentText("Obteniendo ubicación del usuario...")
            //.setSmallIcon(R.drawable.ic_notification)
            .build()

        // Crear un canal de notificación para Android Oreo y versiones posteriores
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Location Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Iniciar el servicio en primer plano
        startForeground(123, notification)

        startLocationUpdates()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000, // interval in milliseconds
                0f, // minimum distance in meters
                locationListener
            )
        }
    }

    private fun stopLocationUpdates() {
        locationManager.removeUpdates(locationListener)
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // Aquí puedes enviar la ubicación a través de la API REST
            sendLocationToApi(location.latitude, location.longitude)
        }

        override fun onProviderDisabled(provider: String) {
            // Manejar si el proveedor de ubicación está deshabilitado
        }

        override fun onProviderEnabled(provider: String) {
            // Manejar si el proveedor de ubicación está habilitado
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            // Manejar cambios en el estado del proveedor de ubicación
        }
    }

    private fun sendLocationToApi(latitude: Double, longitude: Double) {
        var user = User()

        val prefsUser = applicationContext.getSharedPreferences(
            resources.getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )
        user = User(JSONObject(prefsUser!!.getString("user","")))
        CoroutineScope(Dispatchers.Main).launch {
            val result = userRepository.save_location(
                //user_id = user.id.toString(),
                user_id = user.id.toString(),
                latitude = latitude.toString(),
                longitude = longitude.toString()
            )
            when (result) {
                is Result.Success -> {
                    val locationUser = result.data.data
                    val message = result.data.message
                    Log.e("", "sendLocationToApi: "+locationUser.latitude + " "+message+ "\n")
                }
                is Result.Error -> {
                    val errorMessage = result.error
                    Log.e("", "sendLocationToApi: "+errorMessage+ "\n")
                }
                else -> {   }
            }
        }
    }
}
