package com.example.outstartodolistactivity

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.outstartodolistactivity.databinding.ActivityProfileChangeBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class ProfileChangeActivity : AppCompatActivity() {
    val binding: ActivityProfileChangeBinding by lazy {
        ActivityProfileChangeBinding.inflate(layoutInflater)
    }
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val glide = Glide.with(this)
        val selectedImageView = binding.profileImgChange

        // Retrofit Base Url
        val retrofit = Retrofit.Builder().baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitService = retrofit.create(RetrofitService::class.java)

        val imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                imageUri = it.data!!.data
                glide.load(imageUri).into(selectedImageView)
            }
        imagePickerLauncher.launch(  // activity가 실행될때 실행되는 코드 인텐트로 파일열기
            Intent(Intent.ACTION_PICK).apply {
                this.type = MediaStore.Images.Media.CONTENT_TYPE
            }
        )

        binding.changeImgChange.setOnClickListener {
            val file = getRealFile((imageUri!!))
            val requestFile = RequestBody.create(
                MediaType.parse(
                    this.contentResolver.getType(imageUri!!)
                ), file
            )

            val body = MultipartBody.Part.createFormData("image", file!!.name, requestFile)
            val header = HashMap<String, String>()
            val sp = this.getSharedPreferences(
                "user_info",
                Context.MODE_PRIVATE
            )
            val token = sp.getString("token", "")
            val userId = sp.getString("user_id", "")
            val user = RequestBody.create(MultipartBody.FORM, userId)
            header.put("Authorization", "token " + token!!)

            retrofitService.changeProfile(userId!!.toInt(), header, body, user)
                .enqueue(object : Callback<Any> {
                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ProfileChangeActivity, "변경완료", Toast.LENGTH_SHORT)
                                .show()
                            onBackPressed()
                        }
                    }

                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        Toast.makeText(this@ProfileChangeActivity, "변경실패", Toast.LENGTH_SHORT)
                            .show()
                    }
                })

        }

    }

    fun getRealFile(uri: Uri): File? {
        var uri: Uri? = uri
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        var cursor: Cursor? = this.getContentResolver().query(
            uri!!,
            projection,
            null,
            null,
            MediaStore.Images.Media.DATE_MODIFIED + " desc"
        )
        if (cursor == null || cursor.getColumnCount() < 1) {
            return null
        }
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val path: String = cursor.getString(column_index)
        if (cursor != null) {
            cursor.close()
            cursor = null
        }
        return File(path)
    }
}
