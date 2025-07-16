package edu.ucne.skyplanerent.data.remote.dto


data class AeronaveDTO(
    val aeronaveId:Int? = null,
    val estadoId:Int = 0,
    val modeloAvion:String,
    val descripcionCategoria:String,
    val registracion:String,
    val costoXHora:Double?,
    val descripcionAeronave:String,
    val velocidadMaxima:Double?,
    val descripcionMotor:String,
    val capacidadCombustible:Int = 0,
    val consumoXHora:Int = 0,
    val peso:Double?,
    val rango:Int = 0,
    val capacidadPasajeros:Int = 0,
    val altitudMaxima:Int  = 0,
    val licencia:String

)