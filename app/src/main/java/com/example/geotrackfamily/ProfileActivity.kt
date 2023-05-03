package com.example.geotrackfamily

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.geotrackfamily.adapter.FriendAdapter
import com.example.geotrackfamily.databinding.ActivityFindFriendBinding
import com.example.geotrackfamily.databinding.ActivityProfileBinding
import com.example.geotrackfamily.databinding.ToolbarAppBinding
import com.example.geotrackfamily.models.Friend
import com.example.geotrackfamily.models.User
import com.example.geotrackfamily.models.shortUser
import com.example.geotrackfamily.utility.CompositionObj
import com.example.geotrackfamily.viewModels.FriendViewModel
import com.example.geotrackfamily.viewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var user: User
    private lateinit var toast: Toast
    private lateinit var toolbarAppBinding: ToolbarAppBinding
    private lateinit var sharedPref: SharedPreferences
    val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolBar()

        toast = Toast(this)
        sharedPref = this@ProfileActivity.getSharedPreferences(
            getString(R.string.shared_preferences), Context.MODE_PRIVATE)

        try {
            user = User(JSONObject(sharedPref!!.getString("user","")))
        }catch (e: java.lang.Exception){
            logoutUser()
        }


        binding.etEmail.setText(user.email)
        binding.etName.setText(user.name)
        events()
        coroutines()
    }

    fun showToast(message: String){
        toast?.cancel()
        toast = Toast.makeText(this@ProfileActivity,message, Toast.LENGTH_LONG)
        toast.show()
    }

    fun setUpToolBar() {
        toolbarAppBinding = ToolbarAppBinding.bind(binding.root)
        setSupportActionBar(toolbarAppBinding.toolbar)
        toolbarAppBinding.titleBar.text = resources.getString(R.string.profile)
        toolbarAppBinding.profile.isVisible = false
        toolbarAppBinding.rvNotification.isVisible = false
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

    fun events() {
        binding.cvSave.setOnClickListener {
            var user_name = binding.etName.text.toString()
            var user_email = binding.etEmail.text.toString()
            var user_password = binding.etPassword.text.toString()
            if (user_name.trim().length > 0 && user_email.trim().length > 0) {
                user.email = user_email
                user.name = user_name
                userViewModel.update_user(
                    name = user_name,
                    email = user_email,
                    password = if (user_password.trim().length > 0) user_password else ""
                )
                return@setOnClickListener
            }
            showToast(message = resources.getString(R.string.error_fill_user))
        }
        binding.cvLogout.setOnClickListener {
            logoutUser()
        }
    }


    fun coroutines() {
        lifecycleScope.launchWhenCreated {
            userViewModel.loadingProgress.collect {
                binding.linear.isVisible = !it
                binding.progressBar.isVisible = it
            }
        }

        lifecycleScope.launchWhenCreated {
            userViewModel.compositionUpdateUser.collect { result ->
                when(result) {
                    is com.example.geotrackfamily.utility.Result.Success<CompositionObj<shortUser,String>> ->{


                        with(sharedPref?.edit()){
                            var json = JSONObject()
                            json.put("id",user.id)
                            json.put("token",user.token)
                            json.put("name",user.name)
                            json.put("email",user.email)
                            json.put("token_firebase",user.token_firebase.toString())
                            json.put("image",user.image.toString())
                            json.put("code_qr",user.code_qr.toString())
                            json.put("language","es")
                            this?.putString("user",json.toString())
                            this?.putString("token",user.token)
                            this?.putBoolean("islogged",true)
                            this?.apply()
                        }
                        //sharedPref?.edit()?.putString("user",json.toString())
                        //sharedPref?.edit()?.putString("token",user.token)
                        //sharedPref?.edit()?.putBoolean("islogged",true)
                        //sharedPref?.edit()?.apply()

                        showToast(message = result.data.message)
                    }
                    is com.example.geotrackfamily.utility.Result.Error ->
                        showToast(message = result.error)
                    else -> Unit
                }
            }
        }
    }

    fun logoutUser() {
        val editor = sharedPref.edit()
        editor.remove("user")
        editor.remove("token")
        editor.remove("islogged")
        editor.apply()
        startActivity(
            Intent(this@ProfileActivity, MainActivity::class.java)
        )
        finish()
    }
}