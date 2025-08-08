package edu.ucne.skyplanerent.presentation.rutayviajes.tipoVuelo

sealed interface TipoVueloEvent{

    data class TipoVueloChange(val vueloId: Int): TipoVueloEvent
    data class NombreVueloChange(val nombrevuelo: String): TipoVueloEvent
    data class DescripcionTipoVueloChange(val descripciontipovuelo: String): TipoVueloEvent

    data object LimpiarErrorMessageTipoClienteChange: TipoVueloEvent
    data object LimpiarErrorMessageDescripcionTipoVueloChange: TipoVueloEvent
    data object GetTipoVuelos: TipoVueloEvent
    data class GetTipoVuelo(val id: Int): TipoVueloEvent
    data object Save: TipoVueloEvent
    data object Delete: TipoVueloEvent
    data object New: TipoVueloEvent
    data object postTipoVuelo: TipoVueloEvent
    data object ResetSuccessMessage: TipoVueloEvent

}