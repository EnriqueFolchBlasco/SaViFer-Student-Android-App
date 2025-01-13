package com.example.weatherapp.modelo

import com.google.gson.annotations.SerializedName

data class RespuestaClima(

    @SerializedName("main")
    val principal: Principal,

    @SerializedName("weather")
    val clima: List<Clima>,

    @SerializedName("name")
    val nombre: String,

    @SerializedName("rain")
    val lluvia: Lluvia?
)

data class Principal(

    @SerializedName("temp")
    val temperatura: Double,

    @SerializedName("feels_like")
    val temperaturaSensible: Double
)

data class Clima(

    @SerializedName("description")
    val descripcion: String,

    @SerializedName("icon")
    val icono: String
)

data class Lluvia(

    @SerializedName("1h")
    val precipitacion1h: Double? = null,

    @SerializedName("3h")
    val precipitacion3h: Double? = null
)
