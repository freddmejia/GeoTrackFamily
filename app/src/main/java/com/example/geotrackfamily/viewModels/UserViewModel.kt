package com.example.geotrackfamily.viewModels

import androidx.lifecycle.ViewModel
import com.example.geotrackfamily.repository.UserRepository
import javax.inject.Inject

class UserViewModel  @Inject constructor(
    private val repository: UserRepository
)  : ViewModel() {

}