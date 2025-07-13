package edu.ucne.skyplanerent.presentation.aeronave


interface AeronaveEvent {

    data class AeronaveChange(val aeronaveId: Int): AeronaveEvent

    data object Save: AeronaveEvent
    data object Delete: AeronaveEvent
    data object New: AeronaveEvent
}