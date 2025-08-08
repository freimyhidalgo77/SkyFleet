package edu.ucne.skyplanerent.presentation.rutayviajes.tipoVuelo

import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO

data class TipoVueloUiState(
    val tipoVueloId: Int? = null,
    val nombreVuelo: String = "",
    val descripcionTipoVuelo: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorNombreVuelo: String? = null,
    val errorDescripcionTipovuelo: String? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val tipovuelo: List<TipoVueloDTO> = emptyList()
)