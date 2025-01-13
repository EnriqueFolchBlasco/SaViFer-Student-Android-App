package es.efb.isvf_studentapp.retrofit
import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("photo_url")
    val photoUrl: String
)