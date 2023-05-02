package com.example.geotrackfamily.repository

import com.example.geotrackfamily.datasources.FriendRemoteDataSource
import com.example.geotrackfamily.datasources.UserRemoteDataSource
import com.example.geotrackfamily.models.Friend
import com.example.geotrackfamily.models.FriendRequest
import com.example.geotrackfamily.models.User
import com.example.geotrackfamily.models.shortUser
import com.example.geotrackfamily.utility.CompositionObj
import com.example.geotrackfamily.utility.Result
import com.example.geotrackfamily.utility.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class FriendRepository @Inject constructor(
    private val friendRemoteDataSource: FriendRemoteDataSource
) {
    suspend fun fetch_possible_friends(email_name: String): Result<CompositionObj<ArrayList<Friend>, String>> {
        return withContext(Dispatchers.Default){
            try {
                val requestBody: MutableMap<String, String> = HashMap()
                requestBody["email_name"] = email_name
                val response = friendRemoteDataSource.fetchPossibleFriends(requestBody = requestBody)
                val body = response.body()
                if (body != null) {
                    val ab = CompositionObj(response.body()!!.poss_friends, response.body()!!.message)
                    Result.Success(ab)
                }
                else{
                    //val errorBody = (response.errorBody() as HttpException).response()?.errorBody()?.string()
                    Utils.errorResult( message = "",errorBody = response.errorBody()!!)
                }
            }catch (e: Exception){
                Utils.errorResult(message = e.message ?: e.toString())
            }
        }
    }

    suspend fun friend_request(user_id1: String, user_id2: String): Result<CompositionObj<FriendRequest, String>> {
        return withContext(Dispatchers.Default){
            try {
                val requestBody: MutableMap<String, String> = HashMap()
                requestBody["user_id1"] = user_id1
                requestBody["user_id2"] = user_id2
                val response = friendRemoteDataSource.friendRequest(requestBody = requestBody)
                val body = response.body()
                if (body != null) {
                    val ab = CompositionObj(response.body()!!.friend_request, response.body()!!.message)
                    Result.Success(ab)
                }
                else{
                    //val errorBody = (response.errorBody() as HttpException).response()?.errorBody()?.string()
                    Utils.errorResult( message = "",errorBody = response.errorBody()!!)
                }
            }catch (e: Exception){
                Utils.errorResult(message = e.message ?: e.toString())
            }
        }
    }

    suspend fun accept_friend_request(user_id1: String, user_id2: String): Result<CompositionObj<FriendRequest, String>> {
        return withContext(Dispatchers.Default){
            try {
                val requestBody: MutableMap<String, String> = HashMap()
                requestBody["user_id1"] = user_id1
                requestBody["user_id2"] = user_id2
                val response = friendRemoteDataSource.friendRequest(requestBody = requestBody)
                val body = response.body()
                if (body != null) {
                    val ab = CompositionObj(response.body()!!.friend_request, response.body()!!.message)
                    Result.Success(ab)
                }
                else{
                    //val errorBody = (response.errorBody() as HttpException).response()?.errorBody()?.string()
                    Utils.errorResult( message = "",errorBody = response.errorBody()!!)
                }
            }catch (e: Exception){
                Utils.errorResult(message = e.message ?: e.toString())
            }
        }
    }

    suspend fun delete_friend_request(user_id1: String, user_id2: String): Result<CompositionObj<FriendRequest, String>> {
        return withContext(Dispatchers.Default){
            try {
                val requestBody: MutableMap<String, String> = HashMap()
                requestBody["user_id1"] = user_id1
                requestBody["user_id2"] = user_id2
                val response = friendRemoteDataSource.deleteFriendRequest(requestBody = requestBody)
                val body = response.body()
                if (body != null) {
                    val ab = CompositionObj(response.body()!!.friend_request, response.body()!!.message)
                    Result.Success(ab)
                }
                else{
                    //val errorBody = (response.errorBody() as HttpException).response()?.errorBody()?.string()
                    Utils.errorResult( message = "",errorBody = response.errorBody()!!)
                }
            }catch (e: Exception){
                Utils.errorResult(message = e.message ?: e.toString())
            }
        }
    }
}