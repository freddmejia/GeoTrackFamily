package com.example.geotrackfamily.observer


import com.example.geotrackfamily.models.Friend

interface UIObserverLocationFriend {
    fun update(friend: Friend)
}