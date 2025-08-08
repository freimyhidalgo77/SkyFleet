package edu.ucne.skyplanerent.presentation.rutayviajes.ruta

import androidx.compose.ui.graphics.Color
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO

data class RutaUiState(
    val rutaId: Int? = null,
    val origen: String? = null,
    val destino: String? = null,
    val distancia: Double = 0.0,
    val duracionEstimada: Int = 0,
    val rutas: List<RutaDTO> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val errorOrigen: String = "",
    val errorDestino: String = "",
    val errorDistancia: String = "",
    val errorDuracion: String = "",
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val showDialog: Boolean = false,
    val isFormValid: Boolean = false,
    val title: String = "Nueva Ruta",
    val submitButtonText: String = "Guardar",
    val submitButtonContentColor: Color = Color.Gray,
    val submitButtonDisabledContentColor: Color = Color.Gray,
    val submitButtonBorderColor: Color = Color.Gray
)
