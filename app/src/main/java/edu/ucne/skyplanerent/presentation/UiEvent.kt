package edu.ucne.skyplanerent.presentation

sealed class UiEvent {
    object NavigateUp : UiEvent()
    data class ShowSnackbar(val message: String) : UiEvent()
}