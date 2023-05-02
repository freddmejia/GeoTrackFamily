package com.example.geotrackfamily.interfaces

import com.example.geotrackfamily.models.Friend
import com.example.geotrackfamily.models.FriendRequest
import com.example.geotrackfamily.models.GeofenceFriend
import com.example.geotrackfamily.utility.Utils
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

//FriendRemoteDataSource
interface FriendServiceRemote {
    @POST(Utils.fetch_possible_friends)
    suspend fun fetchPossibleFriends(@Body requestBody: Map<String,String>): retrofit2.Response<PossFriendResponseApi>

    @POST(Utils.fetch_friends)
    suspend fun fetchFriends(): retrofit2.Response<FriendsResponseApi>

    @POST(Utils.fetch_friends_request)
    suspend fun fetchFriendsRequest(): retrofit2.Response<RequestFriendResponsArApi>

    @POST(Utils.friend_request)
    suspend fun friendRequest(@Body requestBody: Map<String,String>): retrofit2.Response<RequestFriendResponseApi>

    @POST(Utils.accept_friend_request)
    suspend fun acceptFriendRequest(@Body requestBody: Map<String,String>): retrofit2.Response<RequestFriendResponseApi>

    @POST(Utils.delete_friend_request)
    suspend fun deleteFriendRequest(@Body requestBody: Map<String,String>): retrofit2.Response<RequestFriendResponseApi>

    @POST(Utils.save_geofence_friend)
    suspend fun saveFriendGeofence(@Body requestBody: Map<String,String>): retrofit2.Response<GeofenceFriendResponseApi>

    @POST(Utils.update_geofence_friend)
    suspend fun updateFriendGeofence(@Body requestBody: Map<String,String>): retrofit2.Response<GeofenceFriendResponseApi>

    @POST(Utils.fetch_geofence_byfriend)
    suspend fun fetchFriendGeofence(@Body requestBody: Map<String,String>): retrofit2.Response<GeofenceFriendResponseApi>

    @POST(Utils.delete_geofence_byfriend)
    suspend fun deleteFriendGeofence(@Body requestBody: Map<String,String>): retrofit2.Response<GeofenceFriendResponseApi>

}
class PossFriendResponseApi {
    @SerializedName("possible_friends") var poss_friends: ArrayList<Friend> = arrayListOf()
    @SerializedName("message") var message: String = ""
}

class FriendsResponseApi {
    @SerializedName("friends") var friends: ArrayList<Friend> = arrayListOf()
    @SerializedName("message") var message: String = ""
}

class RequestFriendResponsArApi {
    @SerializedName("friends_requests") var friends_requests: ArrayList<Friend> = arrayListOf()
    @SerializedName("message") var message: String = ""
}

class RequestFriendResponseApi {
    @SerializedName("friend_request") var friend_request: FriendRequest = FriendRequest()
    @SerializedName("message") var message: String = ""
}

class GeofenceFriendResponseApi {
    @SerializedName("geofence_friend") var geofence_friend: GeofenceFriend = GeofenceFriend()
    @SerializedName("message") var message: String = ""
}

