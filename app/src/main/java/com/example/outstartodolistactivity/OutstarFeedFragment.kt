package com.example.outstartodolistactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OutstarFeedFragment : Fragment() {
    lateinit var retrofitService : RetrofitService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.outstar_feed_fragment,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val feedListView = view.findViewById<RecyclerView>(R.id.feed_list)

        // Retrofit Base Url
        val retrofit = Retrofit.Builder().baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofitService = retrofit.create(RetrofitService::class.java)
         retrofitService.getoutstarPosts().enqueue(object :Callback<ArrayList<OutstarPost>>{
            override fun onResponse(
                call: Call<ArrayList<OutstarPost>>,
                response: Response<ArrayList<OutstarPost>>
            ) {
                val postList = response.body()
                val glide = Glide.with(activity!!)
                val adapter =  PostRecyclerViewAdapter(
                    LayoutInflater.from(activity),postList!!,glide,this@OutstarFeedFragment,
                    activity as MainActivity
                )
                feedListView.adapter = adapter
            }

            override fun onFailure(call: Call<ArrayList<OutstarPost>>, t: Throwable) {
            }
        })
    }
    fun postLike (post_id:Int){
        retrofitService.postLike(post_id).enqueue(object :Callback<Any>{
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if(response.isSuccessful){
                    Toast.makeText(activity,"좋아요",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Toast.makeText(activity,"실패",Toast.LENGTH_SHORT).show()
            }
        })
    }
    class PostRecyclerViewAdapter(
        val inflater: LayoutInflater,
        val postList : ArrayList<OutstarPost>,
        val glide: RequestManager,
        val outstarFeedFragment: OutstarFeedFragment,
        val activity: MainActivity
    ):RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHoler>(){
        inner class ViewHoler(itemView :View):RecyclerView.ViewHolder(itemView){
            val ownerImg:ImageView
            val ownerUsername:TextView
            val postImg : ImageView
            val postContent : TextView
            var postAlpha : ImageView
            var postGood : ImageView
            init {
                ownerImg = itemView.findViewById(R.id.ownerImg)
                ownerUsername = itemView.findViewById(R.id.ownerUsername)

                postImg = itemView.findViewById(R.id.postImg)
                postContent = itemView.findViewById(R.id.postContent)
                postAlpha = itemView.findViewById(R.id.postImg_alpha)
                postGood = itemView.findViewById(R.id.postGood)

                    postImg.setOnClickListener {
                        outstarFeedFragment.postLike(postList.get(adapterPosition).id)
                        Thread{
                            activity.runOnUiThread {
                                postAlpha.visibility = VISIBLE
                                postGood.visibility = VISIBLE
                            }
                            Thread.sleep(2000)
                            activity.runOnUiThread {
                                postAlpha.visibility = INVISIBLE
                                postGood.visibility = INVISIBLE
                            }
                    }.start()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHoler {
            val view = inflater.inflate(R.layout.post_item,parent,false)
            return ViewHoler(view)
        }

        override fun onBindViewHolder(holder: ViewHoler, position: Int) {
            postList.get(position).owner_profile.image?.let {
                glide.load(postList.get(position).owner_profile.image).centerCrop().circleCrop().into(holder.ownerImg)
            }
            postList.get(position).image?.let {
                glide.load(postList.get(position).image).centerCrop().into(holder.postImg)
            }
            holder.ownerUsername.text = postList.get(position).owner_profile.username

            holder.postContent.text = postList.get(position).content


        }

        override fun getItemCount(): Int {
            return postList.size
        }
    }
}