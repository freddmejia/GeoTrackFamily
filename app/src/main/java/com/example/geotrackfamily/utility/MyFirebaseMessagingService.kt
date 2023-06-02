package com.example.geotrackfamily.utility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.geotrackfamily.R
import android.util.Log
class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e("", "onMessageReceived: "+remoteMessage.toString() )
        // Obtener el título y el mensaje de la notificación
        val notificationTitle = remoteMessage.notification?.title
        val notificationMessage = remoteMessage.notification?.body

        // Mostrar la notificación
        if (notificationTitle != null && notificationMessage != null) {
            Log.e("", "onMessageReceived: inside"+remoteMessage.toString())
            showNotification(notificationTitle, notificationMessage)
        }
    }


    private fun showNotification(title: String, message: String) {
        // Crear el canal de notificación (solo necesario para Android Oreo y versiones posteriores)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_id"
            val channelName = "My Channel"
            val channelImportance = NotificationManager.IMPORTANCE_DEFAULT

            val notificationChannel = NotificationChannel(channelId, channelName, channelImportance)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Crear la notificación
        val notificationBuilder = NotificationCompat.Builder(this, "my_channel_id")
            .setSmallIcon(R.drawable.notification_on_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Mostrar la notificación
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistrationToServer(token)

    }
    fun sendRegistrationToServer(token: String){
        //Enviar el token al api rest
    }
}