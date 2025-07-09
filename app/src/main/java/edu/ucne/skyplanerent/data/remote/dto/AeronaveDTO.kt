package edu.ucne.skyplanerent.data.remote.dto

data class AeronaveDTO(
    val AeronaveId:Int? = null,
    val estadoId:Int? = null,
    val ModeloAvion:String,
    val DescripcionCategoria:String,
    val Registracion:String,
    val CostoXHora:Double?,
    val DescripcionAeronave:String,
    val VelocidadMaxima:Double?,
    val DescripcionMotor:String,
    val CapacidadCombustible:Int = 0,
    val ConsumoXHora:Int = 0,
    val Peso:Double?,
    val Rango:Int = 0 ,
    val CapacidadPasajeros:Int = 0,
    val AltitudMaxima:Int  = 0,
    val Licencia:String

)
