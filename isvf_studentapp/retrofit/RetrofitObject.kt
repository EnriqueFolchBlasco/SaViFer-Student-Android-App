package es.efb.isvf_studentapp.retrofit

import android.content.Context
import es.efb.isvf_studentapp.utils.PREFERENCES_ACCESS_TOKEN_KEY

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitObject {

    companion object{
        private const val BASE_URL = "http://54.89.173.70:8000/api/v1/"
        private var authInstance: Retrofit? = null
        private var instance: Retrofit? = null

        fun getInstance(): Retrofit{
            synchronized(this){
                if(instance == null){
                    instance = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }
                return instance!!
            }
        }

        fun getAuthInstance(c: Context): Retrofit{
            synchronized(this){
                if(authInstance == null){
                    val prefs = c.getSharedPreferences(
                        "es.efb.android_preferences",
                        Context.MODE_PRIVATE
                    )
                    val token = prefs.getString(PREFERENCES_ACCESS_TOKEN_KEY, "")
                    authInstance = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(OkHttpClient.Builder()
                            .addInterceptor { chain ->
                                val originalRequest = chain.request()
                                val builder = originalRequest.newBuilder()
                                    .header("Authorization", "Bearer $token")
                                val newRequest = builder.build()
                                chain.proceed(newRequest)
                            }
                            .build())
                        .build()
                }

                return authInstance!!
            }
        }


    }


}