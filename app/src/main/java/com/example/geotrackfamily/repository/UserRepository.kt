package com.example.geotrackfamily.repository

import android.util.Log
import com.example.geotrackfamily.datasources.UserRemoteDataSource
import com.example.geotrackfamily.models.User
import com.example.geotrackfamily.utility.CompositionObj
import com.example.geotrackfamily.utility.Utils.Companion.errorResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject
import com.example.geotrackfamily.utility.Result
import org.json.JSONObject
import retrofit2.HttpException

class UserRepository  @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource
) {
    suspend fun login(email: String,password: String): Result<CompositionObj<User, String>> {
        return withContext(Dispatchers.Default){
            try {
                val requestBody: MutableMap<String, String> = HashMap()
                requestBody["email"] = email
                requestBody["password"] = password
                //requestBody["remember_me"] = token
                val response = userRemoteDataSource.login(requestBody = requestBody)
                val body = response.body()
                if (body != null) {
                    val ab = CompositionObj<User,String>(response.body()!!.user, response.body()!!.message)
                    Result.Success(ab)
                }
                else{
                    //val errorBody = (response.errorBody() as HttpException).response()?.errorBody()?.string()
                    errorResult( message = "",errorBody = response.errorBody()!!)
                }
            }catch (e: Exception){
                errorResult(message = e.message ?: e.toString())
            }
        }
    }
}
