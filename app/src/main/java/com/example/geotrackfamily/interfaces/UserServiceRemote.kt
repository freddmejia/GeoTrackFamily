package com.example.geotrackfamily.interfaces

import com.example.geotrackfamily.models.Health
import com.example.geotrackfamily.models.User
import com.example.geotrackfamily.models.shortUser
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