package edu.ucne.skyplanerent.presentation.aeronave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.data.repository.AeronaveRepository
import edu.ucne.skyplanerent.data.repository.ReservaRepository
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaEvent
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AeronaveViewModel @Inject constructor(
    private val aeronaveRepository: AeronaveRepository

):ViewModel() {
    private val _uiState = MutableStateFlow(AeronaveUiState())
    val uiState = _uiState.asStateFlow()

    /*fun onEvent(event: AeronaveEvent) {
        when (event) {
            is AeronaveEvent.AeronaveChange -> TODO()
            AeronaveEvent.Delete -> TODO()
            is RutaEvent.DestinoChange -> TODO()
            is RutaEvent.DistanciaChange -> TODO()
            is RutaEvent.DuracionEstimadaChange -> TODO()
            RutaEvent.New -> TODO()
            is RutaEvent.OrigenChange -> TODO()
            is RutaEvent.RutaChange -> TODO()
            RutaEvent.Save -> TODO()
        }
    }*/

    init {
        getAeronave()
    }


    fun saveAeronave() {
        viewModelScope.launch {
            if (_uiState.value.ModeloAvion.isBlank() || _uiState.value.DescripcionAeronave.isBlank() ||
                _uiState.value.Registracion.isBlank() || _uiState.value.CostoXHora!! == null || _uiState.value.DescripcionMotor.isBlank()) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Los campos deben estar llenos!", successMessage = null
                    )
                }
                return@launch
            }
            try {
                aeronaveRepository.save(_uiState.value.toDTO())
                _uiState.update {
                    it.copy(
                        successMessage = "La ruta se ha guardado con exito!", errorMessage = null
                    )
                }
                //nuevo()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Hubo un error al guardar la ruta", successMessage = null
                    )
                }
            }
        }
    }


    fun findAeronave(aeronaveId: Int) {
        viewModelScope.launch {
            if (aeronaveId > 0) {
                val aeronaveDTO = aeronaveRepository.find(aeronaveId)
                if (aeronaveDTO.AeronaveId != 0) {
                    _uiState.update {
                        it.copy(
                            AeronaveId = aeronaveDTO.AeronaveId,
                            ModeloAvion = aeronaveDTO.ModeloAvion,
                            DescripcionAeronave = aeronaveDTO.DescripcionAeronave,
                            Registracion = aeronaveDTO.Registracion,
                            CostoXHora = aeronaveDTO.CostoXHora,
                            VelocidadMaxima = aeronaveDTO.VelocidadMaxima,
                            DescripcionMotor =  aeronaveDTO.DescripcionMotor,
                            CapacidadCombustible = aeronaveDTO.CapacidadCombustible,
                            ConsumoXHora = aeronaveDTO.ConsumoXHora,
                            DescripcionCategoria = aeronaveDTO.DescripcionCategoria,
                            Peso = aeronaveDTO.Peso,
                            Rango = aeronaveDTO.Rango,
                            CapacidadPasajeros =  aeronaveDTO.CapacidadPasajeros,
                            AltitudMaxima = aeronaveDTO.AltitudMaxima,
                            Licencia = aeronaveDTO.Licencia
                        )
                    }
                }
            }
        }
    }


    private fun delete(id: Int) {
        viewModelScope.launch {
            aeronaveRepository.delete(id)
        }
    }


    fun getAeronave() {
        viewModelScope.launch {
            aeronaveRepository.getAeronave().collectLatest { getting ->
                when (getting) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                Aeronaves = getting.data ?: emptyList(),
                                isLoading = false
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = getting.message
                                    ?: "Hubo un error al cargar las aeronaves",
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }


    /*private fun onAeronaveChange(aeronaveId: Int) {
        _uiState.update {
            it.copy(aeronaveId = aeronaveId)
        }
    }

    private fun onOrigenChange(origen: String) {
        _uiState.update {
            it.copy(origen = origen)
        }
    }

    private fun onDestinoChange(destino: String) {
        _uiState.update {
            it.copy(destino = destino)
        }
    }

    private fun onDistanciaChange(distancia: Double) {
        _uiState.update {
            it.copy(distancia = distancia)
        }
    }

    private fun onDuracionEstimadaChange(duracionEstimada: Int) {
        _uiState.update {
            it.copy(duracionEstimada = duracionEstimada)
        }
    }*/


    fun AeronaveUiState.toDTO() = AeronaveDTO(
        AeronaveId = AeronaveId,
        estadoId =  estadoId?:0,
        ModeloAvion =  ModeloAvion?: "",
        DescripcionAeronave = DescripcionAeronave?: "",
        Registracion = Registracion?: "",
        CostoXHora = CostoXHora?:0.0,
        VelocidadMaxima = VelocidadMaxima?: 0.0,
        DescripcionMotor =  DescripcionMotor?:"",
        CapacidadCombustible = CapacidadCombustible?: 0,
        ConsumoXHora = ConsumoXHora?: 0,
        DescripcionCategoria = DescripcionCategoria?:"",
        Peso = Peso?:0.0,
        Rango = Rango?: 0 ,
        CapacidadPasajeros =  CapacidadPasajeros?: 0,
        AltitudMaxima = AltitudMaxima?: 0,
        Licencia = Licencia?:""

    )

}
