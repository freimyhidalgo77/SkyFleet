package edu.ucne.skyplanerent.data.remote.dto

import android.icu.text.DecimalFormat

data class RutaDTO (
    val RutaId: Int = 0,
    val origen:String,
    val destino:String,
    val distancia:Double?,
    val duracion:Int = 0,

)