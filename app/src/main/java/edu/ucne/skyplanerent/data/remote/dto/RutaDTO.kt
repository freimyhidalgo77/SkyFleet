package edu.ucne.skyplanerent.data.remote.dto

import kotlinx.serialization.SerialName

data class RutaDTO (
    val RutaId: Int = 0,
    val origen:String,
    val destino:String,
    val distancia:Double?,
    val duracion:Int = 0,

    )