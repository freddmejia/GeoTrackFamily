package com.example.geotrackfamily.models

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton


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
    constructor(jsonObject: JSONObject): this(
        jsonObject.getInt("id"),
        jsonObject.getString("name"),
        jsonObject.getString("email"),
        jsonObject.getString("token_firebase"),
        jsonObject.getString("image"),
        jsonObject.getString("code_qr"),
        jsonObject.getString("token")
    )
}


@Singleton
class TokenManager @Inject constructor(private val preferences: SharedPreferences) {

    companion object {
        private const val TOKEN_KEY = "token"
    }

    fun getToken(): String? {
        return preferences.getString(TOKEN_KEY, null)
    }

    fun saveToken(token: String) {
        preferences.edit().putString(TOKEN_KEY, token).apply()
    }

}


class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()
        if (token != null) {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            return chain.proceed(request)
        }
        return chain.proceed(chain.request())
    }

}