package com.example.geotrackfamily.repository

import com.example.geotrackfamily.datasources.UserRemoteDataSource
import javax.inject.Inject

class UserRepository  @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource
) {

}
