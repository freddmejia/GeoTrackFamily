package com.example.geotrackfamily.utility

import okhttp3.ResponseBody
import org.json.JSONObject

class Utils {
    companion object{
        const val api_version = "/api"
        const val domainApi = "http://192.168.1.16:8000/"

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

        //friends
        const val fetch_possible_friends = "$api_version/fetch_possible_friends"
        const val fetch_friends = "$api_version/fetch_friends"
        const val friend_request = "$api_version/friend_request"
        const val accept_friend_request = "$api_version/accept_friend_request"
        const val delete_friend_request = "$api_version/delete_friend_request"
        const val save_geofence_friend = "$api_version/save_geofence_friend"
        const val update_geofence_friend = "$api_version/update_geofence_friend"
        const val fetch_geofence_byfriend = "$api_version/fetch_geofence_byfriend"
        const val delete_geofence_byfriend = "$api_version/delete_geofence_byfriend"

        const val friend_added = 1
        const val are_friend = 2
        const val friend_cancel = 3

        fun <T> errorResult(message: String,errorBody: ResponseBody? = null): Result<T> {
            //Timber.d(message)
            var mess_d = message
            if (errorBody != null) {
                val json = JSONObject(errorBody?.string())
                mess_d = json.getString("message")
            }
            return Result.Error(mess_d)
        }
    }
}