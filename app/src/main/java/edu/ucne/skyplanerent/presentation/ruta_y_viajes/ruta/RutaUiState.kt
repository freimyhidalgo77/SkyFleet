package edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta

import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO

data class RutaUiState(
    val rutaId: Int = 0,
    val aeronaveId: Int = 0,
    val origen: String = "",
    val destino: String = "",
    val distancia: Double = 0.0,
    val duracionEstimada: Int = 0,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val rutas: List<RutaDTO> = emptyList()
)
