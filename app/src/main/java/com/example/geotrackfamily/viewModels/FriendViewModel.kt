package com.example.geotrackfamily.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geotrackfamily.models.Friend
import com.example.geotrackfamily.models.FriendRequest
import com.example.geotrackfamily.models.User
import com.example.geotrackfamily.models.shortUser
import com.example.geotrackfamily.repository.FriendRepository
import com.example.geotrackfamily.repository.UserRepository
import com.example.geotrackfamily.utility.CompositionObj
import com.example.geotrackfamily.utility.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository
)  : ViewModel() {
    private var apiCallJob: Job? = null
    private val inputTextFlow = MutableStateFlow("")

    private val _compositionFetchPossibleFriends = MutableStateFlow<Result<CompositionObj<ArrayList<Friend>, String>>>(Result.Empty)
    val compositionFetchPossibleFriends : StateFlow<Result<CompositionObj<ArrayList<Friend>, String>>> = _compositionFetchPossibleFriends

    private val _loadingProgress = MutableStateFlow(false)
    val loadingProgress: StateFlow<Boolean> = _loadingProgress

    private val _compositionFriendRequest = MutableStateFlow<Result<CompositionObj<FriendRequest, String>>>(Result.Empty)
    val compositionFriendRequest : StateFlow<Result<CompositionObj<FriendRequest, String>>> = _compositionFriendRequest


    fun setInputText(inputText: String) {
        inputTextFlow.value = inputText
    }

    fun fetch_possible_friends() = viewModelScope.launch {
        inputTextFlow
            .debounce(500)
            .collect { inputText ->
                apiCallJob?.cancel()
                apiCallJob = launch {
                    delay(500)
                    if (inputText.length > 0) {
                        _compositionFetchPossibleFriends.value = Result.Empty
                        _loadingProgress.value = true
                        _compositionFetchPossibleFriends.value =
                            friendRepository.fetch_possible_friends(email_name = inputTextFlow.value)
                        _loadingProgress.value = false
                    }
                }
            }
    }

    fun friend_request(user_id1: String, user_id2: String) = viewModelScope.launch {
        _compositionFriendRequest.value = Result.Empty
        _loadingProgress.value = true
        _compositionFriendRequest.value = friendRepository.friend_request(user_id1 = user_id1, user_id2 = user_id2)
        _loadingProgress.value = false
    }

    fun delete_friend_request(user_id1: String, user_id2: String) = viewModelScope.launch {
        _compositionFriendRequest.value = Result.Empty
        _loadingProgress.value = true
        _compositionFriendRequest.value = friendRepository.delete_friend_request(user_id1 = user_id1, user_id2 = user_id2)
        _loadingProgress.value = false
    }

}