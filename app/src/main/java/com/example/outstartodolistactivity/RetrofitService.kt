package com.example.outstartodolistactivity

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

class User(
    val userName: String,
    val token :String,
    val id : Int
)
class OutstarPost(
    val id : Int,
    val content:String,
    val image: String?,
    val owner_profile:OwnerProfile
)
class OwnerProfile(
    val username: String,val image: String?
)
class UserInfo(
    val id : Int,
    val userName : String,
    val profile : OwnerProfile
)
interface RetrofitService {
    @POST("user/login/")
    @FormUrlEncoded
    fun outstarLogin(
        @FieldMap params : HashMap<String,Any>
    ) : retrofit2.Call<User>

    @POST("user/signup/")
    @FormUrlEncoded
    fun outstarJoin(
        @FieldMap params : HashMap<String,Any>
    ) : retrofit2.Call<User>

    @GET("instagram/post/list/all/")
    fun getoutstarPosts(

    ): Call<ArrayList<OutstarPost>>

    @POST("instagram/post/like/{post_id}/")
    fun postLike(
        @Path("post_id") post_id: Int
    ) : Call<Any>

    @Multipart
    @POST("instagram/post/")
    fun uploadPost(
        @HeaderMap headers : Map<String,String>,
        @Part image : MultipartBody.Part,
        @Part("content") content:RequestBody
    ) : Call<Any>

    @GET("user/userInfo/")
    fun getUserInfo(
        @HeaderMap headers: Map<String, String>
    ): Call<UserInfo>

    @Multipart
    @PUT("user/profile/{user_id}/")
    fun changeProfile(
        @Path("user_id") userId : Int,
        @HeaderMap headers: Map<String,String>,
        @Part image : MultipartBody.Part,
        @Part ("user") user : RequestBody,
    ):Call<Any>
}