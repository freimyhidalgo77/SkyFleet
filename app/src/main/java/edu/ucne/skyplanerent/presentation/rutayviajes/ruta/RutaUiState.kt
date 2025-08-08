package edu.ucne.skyplanerent.presentation.rutayviajes.ruta

import edu.ucne.skyplanerent.data.remote.dto.RutaDTO

data class RutaUiState(
    val rutaId: Int? = null,
    val origen: String = "",
    val destino: String = "",
    val distancia: Double = 0.0,
    val duracionEstimada: Int = 0,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorOrigen: String? = null,
    val errorDestino: String? = null,
    val errorDistancia: String? = null,
    val errorDuracionEstimada: String? = null,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val rutas: List<RutaDTO> = emptyList()
)
