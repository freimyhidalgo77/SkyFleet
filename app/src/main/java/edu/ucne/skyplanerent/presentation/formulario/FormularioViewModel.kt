package edu.ucne.skyplanerent.presentation.formulario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.local.entity.FormularioEntity
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.local.repository.FormularioRepository
import edu.ucne.skyplanerent.data.local.repository.RutaRepository
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

    private fun save() {
        viewModelScope.launch {
            if (_uiState.value.nombre.isBlank() || _uiState.value.apellido.isBlank() ||
                _uiState.value.telefono.isBlank() || _uiState.value.correo.isBlank() ||
                _uiState.value.pasaporte.isBlank() ||
                _uiState.value.ciudadResidencia.isBlank())
            {
                _uiState.update {
                    it.copy(errorMessage = "Todos los campos deben ser rellenados")
                }
            } else {
                formularioRepository.saveFormulario(_uiState.value.toEntity())
            }
        }
    }

    private fun nuevoFormulario() {
        _uiState.update {
            it.copy(
                formularioId = null,
                nombre = "",
                apellido = "",
                telefono = "",
                correo = "",
                pasaporte = "",
                ciudadResidencia = "",
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
                    )
                }
            }
        }
    }

    private fun delete() {
        viewModelScope.launch {
            formularioRepository.deleteFormulario(_uiState.value.toEntity())
        }
    }

    private fun getFormulario() {
        viewModelScope.launch {
            formularioRepository.getAll().collect { formulario ->
                _uiState.update {
                    it.copy(formularios = formulario)
                }
            }
        }
    }

    private fun onFormularioChange(formularioId: Int) {
        _uiState.update {
            it.copy(formularioId = formularioId)
        }
    }

    private fun onNombreChange(nombre: String) {
        _uiState.update {
            it.copy(nombre = nombre)
        }
    }

    private fun onApellidoChange(apellido: String) {
        _uiState.update {
            it.copy(apellido = apellido)
        }
    }

    private fun onCorreoChange(correo: String) {
        _uiState.update {
            it.copy(correo = correo)
        }
    }

    private fun onTelefonoChange(telefono: String) {
        _uiState.update {
            it.copy(telefono = telefono)
        }
    }

    private fun onPasaporteChange(pasaporte: String) {
        _uiState.update {
            it.copy(pasaporte = pasaporte)
        }
    }

    private fun onCiudadResidenciaChange(ciudad: String) {
        _uiState.update {
            it.copy(ciudadResidencia = ciudad)
        }
    }

    fun FormularioUiState.toEntity() = FormularioEntity(
        formularioId = formularioId,
        nombre  = nombre?: "",
        apellido = apellido ?: "",
        correo = correo ?: "",
        telefono = telefono ?: "",
        pasaporte = pasaporte ?: "",
        ciudadResidencia = ciudadResidencia ?: ""
    )
}