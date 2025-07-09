package edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo

import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO

data class TipoVueloUiState(
    val tipoVueloId: Int? = null,
    val nombreVuelo: String = "",
    val descripcionTipoVuelo: String = "",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val tipovuelo: List<TipoVueloDTO> = emptyList()
)