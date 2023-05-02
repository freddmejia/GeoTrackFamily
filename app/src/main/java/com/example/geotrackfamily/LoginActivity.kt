package com.example.geotrackfamily

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.geotrackfamily.databinding.ActivityLoginBinding
import com.example.geotrackfamily.models.User
import com.example.geotrackfamily.utility.CompositionObj
import com.example.geotrackfamily.viewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import com.example.geotrackfamily.utility.Result
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var toast: Toast
    val TAG = "LoginActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toast = Toast(this)

        coroutines()
        events()
    }
    fun events(){
        binding.cvLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.trim().isNotEmpty() && password.trim().isNotEmpty()){
                userViewModel.login(email = email, password = password)
                return@setOnClickListener
            }
            showToast(message = resources.getString(R.string.error_fill))
        }
    }

    fun coroutines(){
        lifecycleScope.launchWhenCreated {
            userViewModel.compositionLogin.collect { result ->
                Log.e(TAG, "coroutines: "+result.toString() )
                when(result){
                    is Result.Success<CompositionObj<User,String>> ->{
                        val sharedPref = this@LoginActivity.getSharedPreferences(
                            getString(R.string.shared_preferences), Context.MODE_PRIVATE)
                        with(sharedPref?.edit()){
                            var json = JSONObject()

                            json.put("id",result.data.data.id)
                            json.put("token",result.data.data.token)
                            json.put("name",result.data.data.name)
                            json.put("email",result.data.data.email)
                            json.put("token_firebase",result.data.data.token_firebase.toString())
                            json.put("image",result.data.data.image.toString())
                            json.put("code_qr",result.data.data.code_qr.toString())
                            json.put("language","es")

                            this?.putString("user",json.toString())
                            this?.putString("token",result.data.data.token)
                            this?.putBoolean("islogged",true)
                            this?.apply()
                        }
                        finish()
                        startActivity(
                            Intent(this@LoginActivity, MainAppActivity::class.java)
                        )
                    }
                    is  Result.Error -> {
                        showToast(message =  result.error)
                    }
                    else -> Unit
                }
            }
        }


        lifecycleScope.launchWhenCreated {
            userViewModel.loadingProgress.collect {
                binding.linear.isVisible = !it
                binding.progressBar.isVisible = it
            }
        }
    }

    fun showToast(message: String){
        toast?.cancel()
        toast = Toast.makeText(this@LoginActivity,message,Toast.LENGTH_LONG)
        toast.show()
    }
}