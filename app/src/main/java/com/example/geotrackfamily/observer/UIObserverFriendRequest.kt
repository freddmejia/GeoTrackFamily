package com.example.geotrackfamily.observer

import com.example.geotrackfamily.models.Friend

interface UIObserverFriendRequest {
    fun onAcceptFriend(friend: Friend)
    fun onCancelFriend(friend: Friend)
}