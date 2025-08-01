package edu.ucne.skyplanerent.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    object Home : Screen()

    @Serializable
    object FirstScreen : Screen()

    @Serializable
    object Register : Screen()

    @Serializable
    object Reserva : Screen()

    @Serializable
    object Aeronaves : Screen()

    @Serializable
    object Rutas_y_viajes : Screen()

    @Serializable
    object Perfil : Screen()

    @Serializable
    object Login : Screen()

    @Serializable
    data class AdminPanel(val adminId: Int) : Screen()

    @Serializable
    data class PerfilAdmin(val adminId: Int) : Screen()

    @Serializable
    data object RutaList: Screen()

    @Serializable
    data class Ruta(val rutaId: Int?) : Screen()

    @Serializable
    data object TipoVueloList: Screen()

    @Serializable
    data class TipoVuelo(val tipovueloId: Int?) : Screen()

    @Serializable
    data object AeronaveList: Screen()

    @Serializable
    data class Aeronave(val aeronaveId: Int?) : Screen()

    @Serializable
    data class AeronaveDetailsScreen(val aeronaveId: Int?) : Screen()

    @Serializable
    data class RutaDetailsScreen(val rutaId: Int?) : Screen()

    @Serializable
    data class RutaDetails(val rutaId: Int) : Screen()

    @Serializable
    data class TipoVueloDetails(val tipovueloId: Int) : Screen()

    @Serializable
    data object CategoriaAeronaveList: Screen()

    @Serializable
    data object CategoriaAeronaveReservaList : Screen()

    @Serializable
    data class TipoAeronaveScreenList(val aeronaveId: Int?) : Screen()

    @Serializable
    data class TipoAeronaveDetails(val aeronaveId: Int?) : Screen()

    @Serializable
    data class ReservaRutaDetails(val reservaRutaId: Int) : Screen()

    @Serializable
    data class CategoriaAeronave(val categoriaId: Int?) : Screen()

    @Serializable
    data class PreReserva(val prereservaId: Int) : Screen()

    @Serializable
    data class Formulario(val formularioId: Int) : Screen()

    @Serializable
    data class PagoReserva(val pagoReservaId: Int) : Screen()

    @Serializable
    data class ReservaDetails(val reservaId: Int) : Screen()

    @Serializable
    data class ReservaEdit(val reservaId: Int) : Screen()

    @Serializable
    data class ReservaDelete(val reservaId: Int) : Screen()
}