package com.example.geotrackfamily.models


class shortUser(
    var id: Int,
    var name: String,
    var email: String,
    var token_firebase: String? = null,
    var image: String? = null,
    var code_qr: String? = null,
) {
    constructor() : this(0,"","","","","")
}

class User(
    var id: Int,
    var name: String,
    var email: String,
    var token_firebase: String? = null,
    var image: String? = null,
    var code_qr: String? = null,
    var token: String
) {
    constructor() : this(0,"","","","","","")
}