package com.example.geotrackfamily.interfaces

import com.example.geotrackfamily.models.*
import com.example.geotrackfamily.utility.Utils
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

//RemoteDataSource
interface UserServiceRemote {
    @POST(Utils.login)
    suspend fun loginUser(@Body requestBody: Map<String,String>): retrofit2.Response<UserResponseApi>

    @POST(Utils.register)
    suspend fun registerUser(@Body requestBody: Map<String,String>): retrofit2.Response<UserResponseApi>

    @POST(Utils.fetch_user)
    suspend fun fetchUser(@Body requestBody: Map<String,String>): retrofit2.Response<UserShortResponseApi>

    @POST(Utils.fetch_health_data)
    suspend fun fetchHealthData(@Body requestBody: Map<String,String>): retrofit2.Response<HealthResponseApi>

    @POST(Utils.update_user)
    suspend fun updateUser(@Body requestBody: Map<String,String>): retrofit2.Response<UserShortResponseApi>

    @POST(Utils.update_user_image)
    suspend fun updateUserImage (@Body requestBody: Map<String,String>): retrofit2.Response<UserShortResponseApi>

    @POST(Utils.update_health_data_user)
    suspend fun updateHealthDataUser (@Body requestBody: Map<String,String>): retrofit2.Response<HealthResponseApi>

    @POST(Utils.save_location)
    suspend fun saveLocation (@Body requestBody: Map<String,String>): retrofit2.Response<LocationResponseApi>

    @POST(Utils.update_token)
    suspend fun updateToken (@Body requestBody: Map<String,String>): retrofit2.Response<UserShortResponseApi>

    @POST(Utils.fetch_notifi_by_user)
    suspend fun fetchNotificationByUser (@Body requestBody: Map<String,String>): retrofit2.Response<NotificationResponseApi>

    @POST(Utils.delete_notification)
    suspend fun deleteNotification (@Body requestBody: Map<String,String>): retrofit2.Response<NotificationDResponseApi>

    @POST(Utils.panic_alert)
    suspend fun panicAlert (@Body requestBody: Map<String,String>): retrofit2.Response<PanicAlertResponseApi>

}
class UserResponseApi {
    @SerializedName("user") var user: User = User()
    @SerializedName("message") var message: String = ""
}
class UserShortResponseApi {
    @SerializedName("user") var user: shortUser = shortUser()
    @SerializedName("message") var message: String = ""
}

class HealthResponseApi {
    @SerializedName("health_data") var health_data: Health = Health()
    @SerializedName("message") var message: String = ""
}

class LocationResponseApi {
    @SerializedName("location_user") var location_user: LocationUser = LocationUser()
    @SerializedName("message") var message: String = ""
}

class NotificationResponseApi {
    @SerializedName("notifications") var notifications: ArrayList<Notification> = arrayListOf()
    @SerializedName("message") var message: String = ""
}

class NotificationDResponseApi {
    @SerializedName("notification") var notification: Notification = Notification()
    @SerializedName("message") var message: String = ""
}

class PanicAlertResponseApi {
    @SerializedName("panic_alert") var panic_alert: String = ""
    @SerializedName("message") var message: String = ""
}
