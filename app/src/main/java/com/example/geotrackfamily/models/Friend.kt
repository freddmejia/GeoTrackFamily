package com.example.geotrackfamily.models

class Friend (
    var user: shortUser,
    var is_friend: Int
) {
    constructor() : this(shortUser(0,"","","","",""),0)
}

class FriendRequest (
    var id: Int,
    var user_id1: Int,
    var user_id2: Int,
    var status: Int
) {
    constructor() : this(0,0,0,0)
}

class GeofenceFriend (
    var id: Int,
    var user_id1: Int,
    var user_id2: Int,
    var latitude: String,
    var longitude: String,
    var ratio: String,
    var status: String,
) {
    constructor() : this(0,0,0,"","","","")
}