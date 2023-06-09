package com.example.outstartodolistactivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.example.outstartodolistactivity.databinding.ActivityOutstarJoinBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.HashMap

class OutstarJoinActivity : AppCompatActivity() {
    var id = ""
    var pw = ""
    var pw2 = ""
    val binding : ActivityOutstarJoinBinding by lazy {
        ActivityOutstarJoinBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Retrofit Base Url
        val retrofit = Retrofit.Builder().baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        binding.idIput.doAfterTextChanged { id = it.toString() }
        binding.pwInput1.doAfterTextChanged { pw = it.toString() }
        binding.pwInput2.doAfterTextChanged { pw2 = it.toString() }
        binding.outstarLogin.setOnClickListener {
            startActivity(Intent(this@OutstarJoinActivity,OutstarLoginActivity::class.java))
        }

        binding.outstarJoin.setOnClickListener {

                val user = HashMap<String, Any>()
                user.put("username", id)
                user.put("password1", pw)
                user.put("password2", pw2)
                retrofitService.outstarJoin(user).enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            val user:User = response.body()!!
                            val sharedPreferences =
                                getSharedPreferences("user_info", Context.MODE_PRIVATE)
                            val editor : SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putString("token",user.token)
                            editor.putString("user_id",user.id.toString())
                            editor.commit()
                            startActivity(Intent(this@OutstarJoinActivity,OutstarLoginActivity::class.java))
                        }else{
                            Toast.makeText(this@OutstarJoinActivity,"비밀번호가 일치 하지 않습니다.",Toast.LENGTH_SHORT)
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {

                    }
                })
        }
    }
}