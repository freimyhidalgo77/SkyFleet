package edu.ucne.skyplanerent.presentation.navigation

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

}