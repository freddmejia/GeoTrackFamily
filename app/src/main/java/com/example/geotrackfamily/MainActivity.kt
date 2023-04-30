package com.example.geotrackfamily

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.geotrackfamily.databinding.ActivityLoginBinding
import com.example.geotrackfamily.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        events()
    }

    fun events(){
        binding.cvLogin.setOnClickListener {
            startActivity(
                Intent(this@MainActivity, LoginActivity::class.java)
            )
        }

        binding.cvRegister.setOnClickListener {
            startActivity(
                Intent(this@MainActivity, RegisterActivity::class.java)
            )
        }
    }
}