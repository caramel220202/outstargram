package com.example.outstartodolistactivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OutstarProfileFragment : Fragment() {
    lateinit var userProfileImage : ImageView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.outstar_profile_fragment,container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userProfileImage = view.findViewById(R.id.profileImg)



        view.findViewById<TextView>(R.id.changeImg).setOnClickListener {
            startActivity(Intent(activity as MainActivity , ProfileChangeActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
            Log.d("testt","resume")
        // Retrofit Base Url
        val retrofit = Retrofit.Builder().baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        val header = HashMap<String, String>()
        val sp = (activity as MainActivity).getSharedPreferences(
            "user_info",
            Context.MODE_PRIVATE
        )
        val token = sp.getString("token", "")
        Log.d("outstaa", "" + token)
        header.put("Authorization", "token " + token!!)

        val glide = Glide.with(activity as MainActivity)
        retrofitService.getUserInfo(header).enqueue(object :Callback<UserInfo>{
            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                if (response.isSuccessful){
                    Log.d("testt","respone")
                    val userInfo = response.body()
                    userInfo!!.profile.image?.let {
                        glide.load(userInfo.profile.image).into(userProfileImage)
                        Log.d("testt"," "+ userInfo.profile.image)
                    }
                }
            }

            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
            }
        })
    }
}