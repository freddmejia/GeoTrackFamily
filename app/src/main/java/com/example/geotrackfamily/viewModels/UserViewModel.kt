package com.example.geotrackfamily.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geotrackfamily.models.Notification
import com.example.geotrackfamily.models.User
import com.example.geotrackfamily.models.shortUser
import com.example.geotrackfamily.repository.UserRepository
import com.example.geotrackfamily.utility.CompositionObj
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import com.example.geotrackfamily.utility.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class UserViewModel  @Inject constructor(
    private val userRepository: UserRepository
)  : ViewModel() {

    private val _compositionLogin = MutableStateFlow<Result<CompositionObj<User,String>>>(Result.Empty)
    val compositionLogin :  StateFlow<Result<CompositionObj<User,String>>> = _compositionLogin

    private val _compositionUpdateUser = MutableStateFlow<Result<CompositionObj<shortUser,String>>>(Result.Empty)
    val compositionUpdateUser :  StateFlow<Result<CompositionObj<shortUser,String>>> = _compositionUpdateUser

    private val _compositionNotifications = MutableStateFlow<Result<CompositionObj<ArrayList<Notification>,String>>>(Result.Empty)
    val compositionNotifications :  StateFlow<Result<CompositionObj<ArrayList<Notification>,String>>> = _compositionNotifications


    private val _loadingProgress = MutableStateFlow(false)
    val loadingProgress: StateFlow<Boolean> = _loadingProgress

    fun login(email: String, password: String) = viewModelScope.launch {
        _compositionLogin.value = Result.Empty
        _loadingProgress.value = true
        _compositionLogin.value = userRepository.login(email = email, password = password)
        _loadingProgress.value = false
    }

    fun register(username: String,email: String,password: String,password2: String) = viewModelScope.launch {
        _compositionLogin.value = Result.Empty
        _loadingProgress.value = true
        _compositionLogin.value = userRepository.register(username = username, email = email,password = password, password2 = password2)
        _loadingProgress.value = false
    }

    fun update_user(name: String,email: String, password: String) = viewModelScope.launch {
        _compositionUpdateUser.value = Result.Empty
        _loadingProgress.value = true
        _compositionUpdateUser.value = userRepository.update_user(email = email, password = password, name = name)
        _loadingProgress.value = false
    }

    fun save_location(user_id: String, latitude: String, longitude: String) = viewModelScope.launch (Dispatchers.IO) {
        userRepository.save_location(user_id = user_id, latitude = latitude, longitude = longitude)
    }

    fun update_token(user_id: String, token: String) = viewModelScope.launch (Dispatchers.IO) {
        userRepository.update_token(user_id = user_id, token = token)
    }

    fun fetc_notificatio_by_user(user_id: String) = viewModelScope.launch (Dispatchers.IO) {
        _compositionNotifications.value = Result.Empty
        _loadingProgress.value = true
        _compositionNotifications.value = userRepository.fetc_notificatio_by_user(user_id = user_id)
        _loadingProgress.value = false
    }


}
