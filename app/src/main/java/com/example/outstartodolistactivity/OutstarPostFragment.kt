package com.example.outstartodolistactivity

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class OutstarPostFragment : Fragment() {
    var imageUri: Uri? = null
    var contentInput: String = ""
    lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    lateinit var selectedContent: EditText
    lateinit var selectedImageView: ImageView
    lateinit var upload: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.outstar_post_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initResultLauncher()

        selectedContent = view.findViewById(R.id.contentWord)
        selectedImageView = view.findViewById(R.id.pickerPhoto)
        upload = view.findViewById(R.id.uploadBtn)


    }

    private fun initResultLauncher() {
        val glide = Glide.with(activity as MainActivity)
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                imageUri = it.data!!.data
                glide.load(imageUri).into(selectedImageView)
                Log.d("outstaa", "" + imageUri)
            }
    }

    private fun getRealFile(uri: Uri): File? {
        var uri: Uri? = uri
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        var cursor: Cursor? = (activity as MainActivity).getContentResolver().query(
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

    fun makePost() {
        imagePickerLauncher.launch(  // activity가 실행될때 실행되는 코드 인텐트로 파일열기
            Intent(Intent.ACTION_PICK).apply {
                this.type = MediaStore.Images.Media.CONTENT_TYPE
            }
        )

        selectedContent.doAfterTextChanged {
            contentInput = it.toString()
        }

        // Retrofit Base Url
        val retrofit = Retrofit.Builder().baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitService = retrofit.create(RetrofitService::class.java)


        upload.setOnClickListener {

            val file = getRealFile((imageUri!!))
            val requestFile = RequestBody.create(
                MediaType.parse(
                    (activity as MainActivity).contentResolver.getType(imageUri!!)
                ), file
            )
            val body = MultipartBody.Part.createFormData("image", file!!.name, requestFile)
            val content = RequestBody.create(MultipartBody.FORM, contentInput)
            val header = HashMap<String, String>()
            val sp = (activity as MainActivity).getSharedPreferences(
                "user_info",
                Context.MODE_PRIVATE
            )
            val token = sp.getString("token", "")
            header.put("Authorization", "token " + token!!)
            retrofitService.uploadPost(header, body, content).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    Toast.makeText(activity as MainActivity, "업로드완료", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                }
            })
        }

    }
}