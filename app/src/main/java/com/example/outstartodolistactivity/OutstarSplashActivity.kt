package com.example.outstartodolistactivity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class OutstarSplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outstar_splash)

        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token","empty")
        when(token){
            "empty" -> {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this,OutstarLoginActivity::class.java))
                }, 2000)
            }
            else -> {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this,MainActivity::class.java))
                }, 2000)
            }
        }
    }
}