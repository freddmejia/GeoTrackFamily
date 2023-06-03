package com.example.geotrackfamily.utility

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import androidx.core.app.ActivityCompat
import okhttp3.ResponseBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Utils {
    companion object{

        const val domainIp = "18.234.175.136"

        const val domainApi = "http://$domainIp/"
        const val urlSocket = "http://$domainIp:8001"

        const val api_version = "/api"

        //user
        const val login = "$api_version/login"
        const val register = "$api_version/register"
        const val fetch_user = "$api_version/fetch_user"
        const val fetch_health_data = "$api_version/fetch_health_data"
        const val update_user = "$api_version/update_user"
        const val update_user_image = "$api_version/update_user_image"
        const val update_health_data_user = "$api_version/update_health_data_user"
        const val forgot_password_step_one = "$api_version/forgot_password_step_one"
        const val update_password_step_last = "$api_version/update_password_step_last"
        const val save_location = "$api_version/save_location"
        const val update_token = "$api_version/update_token"
        const val fetch_notifi_by_user = "$api_version/fetch_notifi_by_user"
        const val delete_notification = "$api_version/delete_notification"
        const val panic_alert = "$api_version/panic_alert"

        //friends
        const val fetch_possible_friends = "$api_version/fetch_possible_friends"
        const val fetch_friends = "$api_version/fetch_friends"
        const val fetch_friends_request = "$api_version/fetch_friends_request"

        const val friend_request = "$api_version/friend_request"
        const val accept_friend_request = "$api_version/accept_friend_request"
        const val delete_friend_request = "$api_version/delete_friend_request"
        const val save_geofence_friend = "$api_version/save_geofence_friend"
        const val update_geofence_friend = "$api_version/update_geofence_friend"
        const val fetch_geofence_byfriend = "$api_version/fetch_geofence_byfriend"
        const val delete_geofence_byfriend = "$api_version/delete_geofence_byfriend"
        const val fetch_last_location_user = "$api_version/fetch_last_location_user"
        const val update_time_location = "$api_version/update_time_location"


        const val friend_added = 1
        const val are_friend = 2
        const val friend_cancel = 3

        const val no_data = "No hay datos"
        fun <T> errorResult(message: String,errorBody: ResponseBody? = null): Result<T> {
            //Timber.d(message)
            var mess_d = message
            if (errorBody != null) {
                val json = JSONObject(errorBody?.string())
                mess_d = json.getString("message")
            }
            return Result.Error(mess_d)
        }

        fun permissionLocation(activity: Activity, context: Context) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Se necesita permiso para acceder a la ubicación.")
                    .setTitle("Solicitud de permiso")
                    .setPositiveButton("OK") { _, _ ->
                        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                    }
                val dialog = builder.create()
                dialog.show()
            }
        }

        fun isHora1Greater(hora1: String, hora2: String): Boolean {
            val format = SimpleDateFormat("HH:mm:ss")
            val time1 = format.parse(hora1)
            val time2 = format.parse(hora2)

            return time1.after(time2)
        }

    }
}