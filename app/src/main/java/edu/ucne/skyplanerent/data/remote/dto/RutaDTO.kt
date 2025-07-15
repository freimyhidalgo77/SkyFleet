package edu.ucne.skyplanerent.data.remote.dto

data class RutaDTO (
    val rutaId: Int? = null,
    val aeronaveId: Int = 0,
    val origen:String,
    val destino:String,
    val distancia:Double?,
    val duracion:Int = 0,
    )