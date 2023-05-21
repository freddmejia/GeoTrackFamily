package com.example.geotrackfamily.models

class LocationUser (
    var id: Int,
    var user_id: Int,
    var latitude: String,
    var longitude: String
) {
    constructor() : this(0,0,"","")
}