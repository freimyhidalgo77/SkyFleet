package edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.data.repository.RutaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RutaViewModel @Inject constructor(
    private val rutasRepository: RutaRepository

): ViewModel() {
    private val _uiState = MutableStateFlow(RutaUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: RutaEvent) {
        when (event) {
            is RutaEvent.AeronaveChange -> TODO()
            RutaEvent.Delete -> TODO()
            is RutaEvent.DestinoChange -> TODO()
            is RutaEvent.DistanciaChange -> TODO()
            is RutaEvent.DuracionEstimadaChange -> TODO()
            RutaEvent.New -> TODO()
            is RutaEvent.OrigenChange -> TODO()
            is RutaEvent.RutaChange -> TODO()
            RutaEvent.Save -> TODO()
        }
    }

    init {
        getRuta()
    }


    fun saveRuta() {
        viewModelScope.launch {
            if(_uiState.value.origen.isBlank() || _uiState.value.destino.isBlank() || _uiState.value.distancia!! == null || _uiState.value.duracionEstimada!! == null){
                _uiState.update {
                    it.copy(
                        errorMessage = "Los campos deben estar llenos!", successMessage = null
                    )
                }
                return@launch
            }
            try{
                rutasRepository.save(_uiState.value.toDTO())
                _uiState.update {
                    it.copy(
                        successMessage = "La ruta se ha guardado con exito!", errorMessage = null
                    )
                }
                nuevo()
            }catch(e:Exception){
                _uiState.update {
                    it.copy(
                        errorMessage = "Hubo un error al guardar la ruta", successMessage = null
                    )
                }
            }
        }
    }

    private fun nuevo() {
        _uiState.update {
            it.copy(
                rutaId = null,
                aeronaveId = 0,
                origen = "",
                destino = "",
                distancia = 0.0,
                duracionEstimada = 0,
                errorMessage = null
            )
        }
    }

    fun findRuta(rutaId: Int) {
        viewModelScope.launch {
            if (rutaId > 0) {
                val rutaDto = rutasRepository.find(rutaId)
                if (rutaDto.RutaId!= 0) {
                    _uiState.update {
                        it.copy(
                            rutaId = rutaDto.RutaId,
                            origen = rutaDto.origen,
                            destino = rutaDto.destino,
                            distancia = rutaDto.distancia?:0.0,
                            duracionEstimada = rutaDto.duracion
                        )
                    }
                }
            }
        }
    }

    //deleteRuta
    private fun delete(id:Int) {
        viewModelScope.launch {
            rutasRepository.delete(id)
        }
    }


    fun getRuta() {
        viewModelScope.launch {
            rutasRepository.getRuta().collectLatest { getting ->
                when (getting) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                rutas = getting.data ?: emptyList(),
                                isLoading = false
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = getting.message
                                    ?: "Hubo un error al cargar la ruta",
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }


    private fun onAeronaveChange(aeronaveId: Int) {
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
    }


    fun RutaUiState.toDTO() = RutaDTO(
        RutaId =  rutaId,
        origen = origen ?: "",
        destino = destino ?: "",
        distancia = distancia?: 0.0,
        duracion = duracionEstimada?:0
    )
}
