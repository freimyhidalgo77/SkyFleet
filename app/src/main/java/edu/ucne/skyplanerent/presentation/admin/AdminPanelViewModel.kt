package edu.ucne.skyplanerent.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.local.entity.AeronaveEntity
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.local.entity.UserRegisterAccount
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.dto.AeronaveDTO
import edu.ucne.skyplanerent.data.remote.dto.RutaDTO
import edu.ucne.skyplanerent.data.repository.AeronaveRepository
import edu.ucne.skyplanerent.data.repository.ReservaRepository
import edu.ucne.skyplanerent.data.repository.RutaRepository
import edu.ucne.skyplanerent.data.repository.UserRepository
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveUiState
import edu.ucne.skyplanerent.presentation.aeronave.AeronaveViewModel
import edu.ucne.skyplanerent.presentation.reserva.ReservaViewModel
import edu.ucne.skyplanerent.presentation.reserva.UiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaUiState
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminPanelViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val reservaRepository: ReservaRepository,
    private val rutaRepository: RutaRepository,
    private val aeronaveRepository: AeronaveRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminPanelUiState())
    val uiState: StateFlow<AdminPanelUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                userRepository.getAllUsers(),
                reservaRepository.getAll(),
                rutaRepository.getRutas(),
                aeronaveRepository.getAeronaves()
            ) { users, reservas, rutasResource, aeronavesResource ->
                when (rutasResource) {
                    is Resource.Success -> rutasResource.data
                    is Resource.Error -> rutasResource.data ?: emptyList()
                    is Resource.Loading -> emptyList()
                }?.let {
                    when (aeronavesResource) {
                        is Resource.Success -> aeronavesResource.data
                        is Resource.Error -> aeronavesResource.data ?: emptyList()
                        is Resource.Loading -> emptyList()
                    }?.let { it1 ->
                        AdminPanelUiState(
                            users = users,
                            reservas = reservas,
                            rutas = it,
                            aeronaves = it1,
                            isLoading = rutasResource is Resource.Loading || aeronavesResource is Resource.Loading,
                            errorMessage = when {
                                rutasResource is Resource.Error && rutasResource.data == null ->
                                    rutasResource.message

                                aeronavesResource is Resource.Error && aeronavesResource.data == null ->
                                    aeronavesResource.message

                                else -> null
                            }
                        )
                    }
                }
            }.catch { e ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar datos: ${e.message}"
                    )
                }
            }.collect { state ->
                _uiState.update { currentState -> state!! } // Aseguramos que devuelva AdminPanelUiState
            }
        }
    }
}

data class AdminPanelUiState(
    val users: List<UserRegisterAccount> = emptyList(),
    val reservas: List<ReservaEntity> = emptyList(),
    val rutas: List<RutaDTO> = emptyList(),
    val aeronaves: List<AeronaveDTO> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)