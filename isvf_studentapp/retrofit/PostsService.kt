package es.efb.isvf_studentapp.retrofit

import com.example.weatherapp.modelo.RespuestaClima
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/api/v1/posts/")
    suspend fun getPosts(): Response<List<Post>>

    @GET("data/2.5/weather")
    suspend fun obtenerClimaActual(
        @Query("lat") latitud: Double,
        @Query("lon") longitud: Double,
        @Query("appid") claveApi: String,
        @Query("units") unidades: String,
        @Query("lang") idioma: String
    ): Response<RespuestaClima>

    @POST("/api/v1/posts/")
    suspend fun createPost(@Body post: Post): Response<Post>

}
