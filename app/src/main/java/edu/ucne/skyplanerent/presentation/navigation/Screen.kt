package edu.ucne.skyplanerent.presentation.navigation

import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable
    object Home : Screen

    @Serializable
    object FirstScreen : Screen

    @Serializable
    object Register : Screen

    @Serializable
    data class Login(val loginId: Int) : Screen

    @Serializable
     object  Reserva : Screen

    @Serializable
    object Aeronaves : Screen

    @Serializable
    object Rutas_y_viajes : Screen

    @Serializable
    object Perfil : Screen



    @Serializable
    data class RutaDetails(val rutaId:Int) : Screen

    @Serializable
    data class Formulario(val formularioId:Int) : Screen



}