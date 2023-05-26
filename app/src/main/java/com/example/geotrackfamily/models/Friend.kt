package com.example.geotrackfamily.models

import java.util.function.BinaryOperator

class Friend (
    var id: Int,
    var name: String,
    var email: String,
    var token_firebase: String? = null,
    var image: String? = null,
    var code_qr: String? = null,
    var hour_start: String = "00:00:00",
    var hour_end: String = "00:00:00",
    var is_friend: Int,
    var is_choosed: Boolean = false
) {
    constructor() : this(0,"","","","","","","",0,false)
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
    var zone: String,
    var status: String,
) {
    constructor() : this(0,0,0,"","","","","")
}