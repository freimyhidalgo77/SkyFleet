package edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta

import edu.ucne.skyplanerent.data.local.entity.RutaEntity

data class RutaUiState(
    val rutaId: Int? = null,
    val aeronaveId: Int = 0,
    val origen: String = "",
    val destino: String = "",
    val distancia: String = "",
    val duracionEstimada: String = "",
    val errorMessage: String? = null,
    val rutas: List<RutaEntity> = emptyList()
)
