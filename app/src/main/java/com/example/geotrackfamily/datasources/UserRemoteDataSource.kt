package com.example.geotrackfamily.datasources

import com.example.geotrackfamily.interfaces.UserServiceRemote
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val userService: UserServiceRemote
) {
    suspend fun login(requestBody: MutableMap<String, String>) = userService.loginUser(requestBody)
    suspend fun registerUser(requestBody: MutableMap<String, String>) = userService.registerUser(requestBody)
    suspend fun fetchUser(requestBody: MutableMap<String, String>) = userService.fetchUser(requestBody)
    suspend fun fetchHealthData(requestBody: MutableMap<String, String>) = userService.fetchHealthData(requestBody)
    suspend fun updateUser(requestBody: MutableMap<String, String>) = userService.updateUser(requestBody)
    suspend fun updateUserImage(requestBody: MutableMap<String, String>) = userService.updateUserImage(requestBody)
    suspend fun updateHealthDataUser(requestBody: MutableMap<String, String>) = userService.updateHealthDataUser(requestBody)
    suspend fun saveLocation(requestBody: MutableMap<String, String>) = userService.saveLocation(requestBody)
    suspend fun updateToken(requestBody: MutableMap<String, String>) = userService.updateToken(requestBody)
    suspend fun fetchNotificationByUser(requestBody: MutableMap<String, String>) = userService.fetchNotificationByUser(requestBody)
    suspend fun deleteNotification(requestBody: MutableMap<String, String>) = userService.deleteNotification(requestBody)
    suspend fun panicAlert(requestBody: MutableMap<String, String>) = userService.panicAlert(requestBody)


}