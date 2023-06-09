package com.example.outstartodolistactivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.example.outstartodolistactivity.databinding.ActivityLoginBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class OutstarLoginActivity : AppCompatActivity() {
    var id :String = ""
    var pw : String = ""
    val binding : ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Retrofit Base Url
        val retrofit = Retrofit.Builder().baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        binding.idIput.doAfterTextChanged {
            id = it.toString()
        }
        binding.pwInput.doAfterTextChanged {
            pw = it.toString()

        binding.login.setOnClickListener{
        val user = HashMap<String,Any>()
            user.put("username",id)
            user.put("password",pw)
        retrofitService.outstarLogin(user).enqueue(object : Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
            if(response.isSuccessful) {

                val user:User =response.body()!!
                val sharedPreferences =
                    getSharedPreferences("user_info",Context.MODE_PRIVATE)
                val editor :SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("token",user.token)
                editor.putString("user_id",user.id.toString())
                editor.commit()
                startActivity(Intent(this@OutstarLoginActivity,MainActivity::class.java))
            }else{
                Toast.makeText(this@OutstarLoginActivity, "로그인정보가 옳지 않습니다.", Toast.LENGTH_SHORT).show()
            }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("testt","" + t)

            }
        })
        }
        }

        binding.outstarJoin.setOnClickListener {
            val intent = Intent(
                this@OutstarLoginActivity,OutstarJoinActivity::class.java)
            startActivity(intent)
        }

    }
}