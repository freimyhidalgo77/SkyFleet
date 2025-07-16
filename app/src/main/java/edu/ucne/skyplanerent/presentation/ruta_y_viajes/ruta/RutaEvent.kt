package edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta

sealed interface RutaEvent{

    data class RutaChange(val rutaId: Int): RutaEvent
    data class OrigenChange(val origen: String): RutaEvent
    data class DestinoChange(val destino: String): RutaEvent
    data class DistanciaChange(val distancia: Double): RutaEvent
    data class DuracionEstimadaChange(val duracionEstimada: Int): RutaEvent


    data object LimpiarErrorMessageOrigenChange: RutaEvent
    data object LimpiarErrorMessageDestinoChange: RutaEvent
    data object LimpiarErrorMessageDistanciaChange: RutaEvent
    data object LimpiarErrorMessageDuracionEstimadaChange: RutaEvent
    data object GetRutas: RutaEvent
    data class GetRuta(val id: Int): RutaEvent
    data object Save: RutaEvent
    data object Delete: RutaEvent
    data object New: RutaEvent
    data object PostRuta: RutaEvent
    data object ResetSuccessMessage: RutaEvent

}