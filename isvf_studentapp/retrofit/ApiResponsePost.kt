package es.efb.isvf_studentapp.retrofit

import com.google.gson.annotations.SerializedName

data class ApiResponsePost(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val posts: List<Post>
)

