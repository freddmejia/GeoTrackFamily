package com.example.geotrackfamily.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geotrackfamily.models.User
import com.example.geotrackfamily.repository.UserRepository
import com.example.geotrackfamily.utility.CompositionObj
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import com.example.geotrackfamily.utility.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel
class UserViewModel  @Inject constructor(
    private val userRepository: UserRepository
)  : ViewModel() {

    private val _compositionLogin = MutableStateFlow<Result<CompositionObj<User,String>>>(Result.Empty)
    val compositionLogin :  StateFlow<Result<CompositionObj<User,String>>> = _compositionLogin

    private val _loadingProgress = MutableStateFlow(false)
    val loadingProgress: StateFlow<Boolean> = _loadingProgress

    fun login(email: String, password: String) = viewModelScope.launch {
        _compositionLogin.value = Result.Empty
        _loadingProgress.value = true
        _compositionLogin.value = userRepository.login(email = email, password = password)
        _loadingProgress.value = false
    }

}
