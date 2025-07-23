package edu.ucne.skyplanerent.presentation.ruta_y_viajes.formulario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.test.core.app.canTakeScreenshot
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.local.entity.FormularioEntity
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.repository.FormularioRepository
import edu.ucne.skyplanerent.data.repository.RutaRepository
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaEvent
import edu.ucne.skyplanerent.presentation.ruta_y_viajes.ruta.RutaUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.Normalizer.Form
import javax.inject.Inject

@HiltViewModel
class FormularioViewModel @Inject constructor(
    private val formularioRepository: FormularioRepository

): ViewModel() {
    private val _uiState = MutableStateFlow(FormularioUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: FormularioEvent) {
        when (event) {
            is FormularioEvent.NombreChange -> TODO()
            FormularioEvent.Delete -> TODO()
            is FormularioEvent.NombreChange -> TODO()
            is FormularioEvent.ApellidoChange -> TODO()
            is FormularioEvent.CorreoChange -> TODO()
            is FormularioEvent.TelefonoChange -> TODO()
            is FormularioEvent.PasaporteChange -> TODO()
            is FormularioEvent.CiudadResidenciaChange -> TODO()
            FormularioEvent.New -> TODO()
            is FormularioEvent.FormularioChange -> TODO()
            FormularioEvent.Save -> TODO()
        }
    }

    init {
        getFormulario()
    }

    fun saveAndReturnId(onSaved: (Int) -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value

            if (state.nombre.isBlank() || state.apellido.isBlank() ||
                state.telefono.isBlank() || state.correo.isBlank() ||
                state.pasaporte.isBlank() || state.ciudadResidencia.isBlank() ||
                state.cantidadPasajeros == 0

            ) {
                _uiState.update {
                    it.copy(errorMessage = "Todos los campos deben ser rellenados")
                }
                return@launch
            }

            val id = formularioRepository.saveFormulario(state.toEntity())
            _uiState.update {
                it.copy(errorMessage = null)
            }

            onSaved(id)
        }
    }



    fun nuevoFormulario() {
        _uiState.update {
            it.copy(
                formularioId = null,
                nombre = "",
                apellido = "",
                telefono = "",
                correo = "",
                pasaporte = "",
                ciudadResidencia = "",
                cantidadPasajeros = 0,
                errorMessage = null
            )
        }
    }

    fun selectedFormulario(formularioId: Int) {
        viewModelScope.launch {
            if (formularioId > 0) {
                val formulario = formularioRepository.findFormulario(formularioId)
                _uiState.update {
                    it.copy(
                        formularioId = formulario?.formularioId,
                        nombre = formulario?.nombre ?: "",
                        apellido = formulario?.apellido ?: "",
                        correo = formulario?.correo ?: "",
                        telefono = formulario?.telefono ?: "",
                        pasaporte = formulario?.pasaporte ?: "",
                        ciudadResidencia = formulario?.ciudadResidencia ?: "",
                        cantidadPasajeros = formulario?.cantidadPasajeros?:0
                    )
                }
            }
        }
    }

     fun deleteFormulario() {
        viewModelScope.launch {
            formularioRepository.deleteFormulario(_uiState.value.toEntity())
        }
    }

     fun getFormulario() {
        viewModelScope.launch {
            formularioRepository.getAll().collect { formulario ->
                _uiState.update {
                    it.copy(formularios = formulario)
                }
            }
        }
    }

     fun onFormularioChange(formularioId: Int) {
        _uiState.update {
            it.copy(formularioId = formularioId)
        }
    }

     fun onNombreChange(nombre: String) {
        _uiState.update {
            it.copy(nombre = nombre)
        }
    }

    fun onApellidoChange(apellido: String) {
        _uiState.update {
            it.copy(apellido = apellido)
        }
    }

     fun onCorreoChange(correo: String) {
        _uiState.update {
            it.copy(correo = correo)
        }
    }

      fun onTelefonoChange(telefono: String) {
        _uiState.update {
            it.copy(telefono = telefono)
        }
    }

      fun onPasaporteChange(pasaporte: String) {
        _uiState.update {
            it.copy(pasaporte = pasaporte)
        }
    }

      fun onCiudadResidenciaChange(ciudad: String) {
        _uiState.update {
            it.copy(ciudadResidencia = ciudad)
        }
    }

    fun onChangePasajero(pasajero: Int) {
        _uiState.update {
            it.copy(cantidadPasajeros = pasajero)
        }
    }

    fun FormularioUiState.toEntity() = FormularioEntity(
        formularioId = formularioId,
        nombre  = nombre?: "",
        apellido = apellido ?: "",
        correo = correo ?: "",
        telefono = telefono ?: "",
        pasaporte = pasaporte ?: "",
        ciudadResidencia = ciudadResidencia ?: "",
        cantidadPasajeros = cantidadPasajeros?:0
    )
}