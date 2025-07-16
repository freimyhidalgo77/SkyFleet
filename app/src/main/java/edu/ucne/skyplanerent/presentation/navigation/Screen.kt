package edu.ucne.skyplanerent.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Screen {

    @Serializable
    object Home : Screen()

    @Serializable
    object FirstScreen : Screen()

    @Serializable
    object Register : Screen()

    @Serializable
    data class Login(val loginId: Int) : Screen()

    @Serializable
     object  Reserva : Screen()

    @Serializable
    object Aeronaves : Screen()

    @Serializable
    object Rutas_y_viajes : Screen()

    @Serializable
    object Perfil : Screen()

    @Serializable
    object AdminPanel : Screen()

    @Serializable
    data object RutaList: Screen()

    @Serializable
    data class Ruta(val rutaId: Int?): Screen()

    @Serializable
    data object TipoVueloList: Screen()

    @Serializable
    data class TipoVuelo(val rutaId: Int?): Screen()

    @Serializable
    data object AeronaveList: Screen()

    @Serializable
    data class Aeronave(val rutaId: Int?): Screen()

    @Serializable
    data class RutaDetails(val rutaId:Int) : Screen()

    @Serializable
    data object CategoriaAeronaveList : Screen()

    @Serializable
    data class CategoriaAeronave(val categoriaId: Int?) : Screen()

}