package com.example.geotrackfamily.repository

import com.example.geotrackfamily.datasources.FriendRemoteDataSource
import com.example.geotrackfamily.datasources.UserRemoteDataSource
import javax.inject.Inject

class FriendRepository @Inject constructor(
    private val friendRemoteDataSource: FriendRemoteDataSource
) {

}