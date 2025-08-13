package edu.ucne.skyplanerent.presentation.reserva

sealed interface ReservaEvent{
    data object Save : ReservaEvent
}
