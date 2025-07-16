package edu.ucne.skyplanerent.presentation.categoriaaeronave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.skyplanerent.data.local.entity.CategoriaAeronaveEntity
import edu.ucne.skyplanerent.data.repository.CategoriaAeronaveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CategoriaAeronaveViewModel @Inject constructor(
    private val categoriaAeronaveRepository: CategoriaAeronaveRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoriaAeronaveUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: CategoriaAeronaveEvent) {
        when (event) {
            CategoriaAeronaveEvent.Delete -> delete()
            CategoriaAeronaveEvent.New -> nuevo()
            is CategoriaAeronaveEvent.DescripcionCategoriaChange -> onDescripcionCategoriaChange(event.descripcionCategoria)
            CategoriaAeronaveEvent.Save -> save()
            is CategoriaAeronaveEvent.CategoriaIdChange -> onCategoriaIdChange(event.categoriaId)
        }
    }

    init {
        getCategoria()
    }

    // saveCategoria
    private fun save() {
        viewModelScope.launch {
            if (_uiState.value.descripcionCategoria.isNullOrBlank()) {
                _uiState.update {
                    it.copy(errorMessage = "La descripción de la categoría es obligatoria")
                }
            } else {
                categoriaAeronaveRepository.saveCategoriaAeronave(_uiState.value.toEntity())
                _uiState.update { it.copy(errorMessage = null) } // Limpiar error tras éxito
            }
        }
    }

    private fun nuevo() {
        _uiState.update {
            it.copy(
                categoriaId = null,
                descripcionCategoria = "",
                errorMessage = null,
                categorias = emptyList() // Reiniciar la lista si aplica
            )
        }
    }

    // findCategoria
    fun selectedCategoria(categoriaId: Int) {
        viewModelScope.launch {
            if (categoriaId > 0) {
                val categoria = categoriaAeronaveRepository.find(categoriaId)
                _uiState.update {
                    it.copy(
                        categoriaId = categoria?.categoriaId,
                        descripcionCategoria = categoria?.descripcionCategoria ?: ""
                    )
                }
            }
        }
    }

    // deleteCategoria
    private fun delete() {
        viewModelScope.launch {
            categoriaAeronaveRepository.deleteCategoriaAeronave(_uiState.value.toEntity())
        }
    }

    private fun getCategoria() {
        viewModelScope.launch {
            categoriaAeronaveRepository.getAll().collect { categorias ->
                _uiState.update {
                    it.copy(categorias = categorias)
                }
            }
        }
    }

    private fun onDescripcionCategoriaChange(descripcionCategoria: String) {
        _uiState.update {
            it.copy(descripcionCategoria = descripcionCategoria)
        }
    }

    private fun onCategoriaIdChange(categoriaId: Int) {
        _uiState.update {
            it.copy(categoriaId = categoriaId)
        }
    }
}

fun CategoriaAeronaveUiState.toEntity() = CategoriaAeronaveEntity(
    categoriaId = categoriaId ?: 0,
    descripcionCategoria = descripcionCategoria ?: ""
)