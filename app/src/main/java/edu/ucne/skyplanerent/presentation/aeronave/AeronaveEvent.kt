package edu.ucne.skyplanerent.presentation.aeronave

import android.net.Uri


sealed interface AeronaveEvent {

    data class AeronaveChange(val aeronaveId: Int): AeronaveEvent
    data class EstadoIdChange(val estadoId: Int?): AeronaveEvent
    data class ModeloAvionChange(val modeloAvion: String): AeronaveEvent
    data class DescripcionCategoriaChange(val descripcionCategoria: String): AeronaveEvent
    data class RegistracionChange(val registracion: String): AeronaveEvent
    data class CostoXHoraChange(val costoXHora: Double?): AeronaveEvent
    data class DescripcionAeronaveChange(val descripcionAeronave: String): AeronaveEvent
    data class VelocidadMaximaChange(val velocidadMaxima: Double?): AeronaveEvent
    data class DescripcionMotorChange(val descripcionMotor: String): AeronaveEvent
    data class CapacidadCombustibleChange(val capacidadCombustible: Int): AeronaveEvent
    data class ConsumoXHoraChange(val consumoXHora: Int): AeronaveEvent
    data class PesoChange(val peso: Double?): AeronaveEvent
    data class RangoChange(val rango: Int): AeronaveEvent
    data class CapacidadPasajerosChange(val capacidadPasajeros: Int): AeronaveEvent
    data class AltitudMaximaChange(val altitudMaxima: Int): AeronaveEvent
    data class LicenciaChange(val licencia: String): AeronaveEvent
    data class ImageSelected(val uri: Uri) : AeronaveEvent // Nuevo evento

    data object LimpiarErrorMessageEstadoIdChange: AeronaveEvent
    data object LimpiarErrorMessageModeloAvionChange: AeronaveEvent
    data object LimpiarErrorMessageDescripcionCategoriaChange: AeronaveEvent
    data object LimpiarErrorMessageRegistracionChange: AeronaveEvent
    data object LimpiarErrorMessageCostoXHoraChange: AeronaveEvent
    data object LimpiarErrorMessageDescripcionAeronaveChange: AeronaveEvent
    data object LimpiarErrorMessageVelocidadMaximaChange: AeronaveEvent
    data object LimpiarErrorMessageDescripcionMotorChange: AeronaveEvent
    data object LimpiarErrorMessageCapacidadCombustibleChange: AeronaveEvent
    data object LimpiarErrorMessageConsumoXHoraChange: AeronaveEvent
    data object LimpiarErrorMessagePesoChange: AeronaveEvent
    data object LimpiarErrorMessageRangoChange: AeronaveEvent
    data object LimpiarErrorMessageCapacidadPasajerosChange: AeronaveEvent
    data object LimpiarErrorMessageAltitudMaximaChange: AeronaveEvent
    data object LimpiarErrorMessageLicenciaChange: AeronaveEvent
    data object GetAeronaves: AeronaveEvent
    data class GetAeronave(val id: Int): AeronaveEvent
    data object Save: AeronaveEvent
    data object Delete: AeronaveEvent
    data object New: AeronaveEvent
    data object PostAeronave: AeronaveEvent
    data object ResetSuccessMessage: AeronaveEvent
    data class FilterByCategoria(val categoriaId: Int) : AeronaveEvent // Nuevo evento
    data class ImageUrlChange(val url: String) : AeronaveEvent // Nuevo evento

}