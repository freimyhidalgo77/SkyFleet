package edu.ucne.skyplanerent.presentation.aeronave

import android.net.Uri
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
    val errorEstadoId: String? = null,
    val errorModeloAvion: String? = null,
    val errorDescripcionCategoria: String? = null,
    val errorRegistracion: String? = null,
    val errorCostoXHora: String? = null,
    val errorDescripcionAeronave: String? = null,
    val errorVelocidadMaxima: String? = null,
    val errorDescripcionMotor: String? = null,
    val errorCapacidadCombustible: String? = null,
    val errorConsumoXHora: String? = null,
    val errorPeso: String? = null,
    val errorRango: String? = null,
    val errorCapacidadPasajeros: String? = null,
    val errorAltitudMaxima: String? = null,
    val errorLicencia: String? = null,
    val successMessage:String? = null,
    val errorMessage:String? = null,
    val isLoading:Boolean = false,
    val isSuccess: Boolean = false,
    val aeronaves:List<AeronaveDTO> = emptyList(),
    val imageUri: Uri? = null, // Nuevo campo para la imagen
    val categoriaId: Int? = null // Nueva propiedad para almacenar el ID de la categoría

)