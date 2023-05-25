package com.example.geotrackfamily

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.geotrackfamily.databinding.ActivityLoginBinding
import com.example.geotrackfamily.databinding.OnboardingToolbarBinding
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
    private lateinit var onboardingToolbarBinding: OnboardingToolbarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolBar()
        toast = Toast(this)

        coroutines()
        events()
    }

    fun setUpToolBar() {
        onboardingToolbarBinding = OnboardingToolbarBinding.bind(binding.root)
        setSupportActionBar(onboardingToolbarBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->
            {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
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
                        val inttent = Intent(this@LoginActivity, MainAppActivity::class.java)
                        inttent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(
                            inttent
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