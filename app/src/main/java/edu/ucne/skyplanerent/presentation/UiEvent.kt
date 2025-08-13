package edu.ucne.skyplanerent.presentation

sealed class UiEvent {
    data object NavigateUp : UiEvent()
    data class ShowSnackbar(val message: String) : UiEvent()
}