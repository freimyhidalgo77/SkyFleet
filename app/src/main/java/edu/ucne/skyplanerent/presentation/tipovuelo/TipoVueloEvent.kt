package edu.ucne.skyplanerent.presentation.tipovuelo

sealed interface TipoVueloEvent{

    data class TipoVueloChange(val vueloId: Int): TipoVueloEvent
    data class RutaChange(val rutaId: Int): TipoVueloEvent
    data class TipoClienteChange(val clienteId: Int): TipoVueloEvent
    data class DescripcionTipoVueloChange(val descripciontipovuelo: String): TipoVueloEvent
    data class PrecioChange(val precio: Double): TipoVueloEvent
    data object Save: TipoVueloEvent
    data object Delete: TipoVueloEvent
    data object New: TipoVueloEvent

}