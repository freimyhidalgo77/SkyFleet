package edu.ucne.skyplanerent.presentation.aeronave

import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO

data class AeronaveUiState (
    val AeronaveId:Int? = null,
    val estadoId:Int? = 0,
    val ModeloAvion:String = "",
    val DescripcionCategoria:String = "",
    val Registracion:String = "",
    val CostoXHora:Double? = 0.0,
    val DescripcionAeronave:String = "",
    val VelocidadMaxima:Double? = 0.0,
    val DescripcionMotor:String = "",
    val CapacidadCombustible:Int = 0,
    val ConsumoXHora:Int = 0,
    val Peso:Double? = 0.0,
    val Rango:Int = 0 ,
    val CapacidadPasajeros:Int = 0,
    val AltitudMaxima:Int  = 0,
    val Licencia:String = "",
    val successMessage:String? = null,
    val errorMessage:String? = null,
    val isLoading:Boolean = false,
    val Aeronaves:List<AeronaveDTO> = emptyList()

)