package edu.ucne.skyplanerent.presentation.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.remote.Resource
import edu.ucne.skyplanerent.data.remote.dto.AdminDTO
import edu.ucne.skyplanerent.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    val adminRepository: AdminRepository
) : ViewModel() {
    private val _adminState = MutableStateFlow(AdminState())
    val adminState: StateFlow<AdminState> = _adminState.asStateFlow()

    fun loadAdminData(adminId: Int) {
        viewModelScope.launch {
            adminId?.let { id ->
                adminRepository.getAdmin(id).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            Log.d("AdminViewModel", "Cargando admin con ID: $id")
                            _adminState.update { it.copy(isLoading = true) }
                        }
                        is Resource.Success -> {
                            Log.d("AdminViewModel", "Admin cargado: ${resource.data}")
                            _adminState.update {
                                it.copy(
                                    isLoading = false,
                                    admin = resource.data?.firstOrNull(),
                                    errorMessage = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            Log.e("AdminViewModel", "Error al cargar admin: ${resource.message}")
                            _adminState.update {
                                it.copy(
                                    isLoading = false,
                                    admin = null,
                                    errorMessage = resource.message
                                )
                            }
                        }
                    }
                }
            } ?: run {
                Log.w("AdminViewModel", "ID de administrador inválido")
                _adminState.update {
                    it.copy(
                        isLoading = false,
                        admin = null,
                        errorMessage = "ID de administrador inválido"
                    )
                }
            }
        }
    }
}

data class AdminState(
    val isLoading: Boolean = false,
    val admin: AdminDTO? = null,
    val errorMessage: String? = null
)