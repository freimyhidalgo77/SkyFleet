package edu.ucne.skyplanerent.presentation.ruta

sealed interface RutaEvent{

    data class RutaChange(val rutaId: Int): RutaEvent
    data class AeronaveChange(val aeronaveId: Int): RutaEvent
    data class OrigenChange(val origen: String): RutaEvent
    data class DestinoChange(val destino: String): RutaEvent
    data class DistanciaChange(val distancia: String): RutaEvent
    data class DuracionEstimadaChange(val duracionEstimada: String): RutaEvent
    data object Save: RutaEvent
    data object Delete: RutaEvent
    data object New: RutaEvent

}
