package edu.ucne.skyplanerent.presentation.ruta_y_viajes.tipoVuelo

import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity

data class TipoVueloUiState(
    val vueloId: Int? = null,
    val rutaId: Int = 0,
    val tipoclienteId: Int = 0,
    val descripcionTipoVuelo: String = "",
    val precio: Double = 0.0,
    val errorMessage: String? = null,
    val tipovuelo: List<TipoVueloEntity> = emptyList()
)