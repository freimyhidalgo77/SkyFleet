package edu.ucne.skyplanerent.presentation.categoriaaeronave

import android.content.Context
import android.net.Uri
import androidx.benchmark.json.BenchmarkData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.ucne.skyplanerent.data.local.entity.CategoriaAeronaveEntity
import edu.ucne.skyplanerent.data.repository.CategoriaAeronaveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


@HiltViewModel
class CategoriaAeronaveViewModel @Inject constructor(
    private val categoriaAeronaveRepository: CategoriaAeronaveRepository,
    @ApplicationContext private val context: Context
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
            is CategoriaAeronaveEvent.ImageSelected -> onImageSelected(event.uri)
        }
    }

    init {
        getCategoria()
    }

    private fun onImageSelected(uri: Uri) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    private fun save() {
        viewModelScope.launch {
            if (_uiState.value.descripcionCategoria.isNullOrBlank()) {
                _uiState.update {
                    it.copy(errorMessage = "La descripción de la categoría es obligatoria")
                }
            } else {
                // Procesar la imagen si existe
                val imagePath = _uiState.value.imageUri?.let { uri ->
                    saveImage(context, uri)
                }
                // Guardar en el repositorio
                categoriaAeronaveRepository.saveCategoriaAeronave(_uiState.value.toEntity(imagePath.toString()))
                _uiState.update { it.copy(errorMessage = null, imageUri = null) } // Limpiar error e imagen tras éxito
            }
        }
    }

    private fun saveImage(context: Context, uri: Uri): String? {
        return try {
            val contentResolver = context.contentResolver
            val file = File(context.filesDir, "categoria_${System.currentTimeMillis()}.jpg")
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = "Error al guardar la imagen: ${e.message}") }
            null
        }
    }

    private fun nuevo() {
        _uiState.update {
            it.copy(
                categoriaId = null,
                descripcionCategoria = "",
                errorMessage = null,
                imageUri = null, // Reiniciar la imagen
                categorias = emptyList()
            )
        }
    }

    fun selectedCategoria(categoriaId: Int) {
        viewModelScope.launch {
            if (categoriaId > 0) {
                val categoria = categoriaAeronaveRepository.find(categoriaId)
                _uiState.update {
                    it.copy(
                        categoriaId = categoria?.categoriaId,
                        descripcionCategoria = categoria?.descripcionCategoria ?: "",
                        imageUri = categoria?.imagePath?.let { path -> Uri.fromFile(File(path)) }
                    )
                }
            }
        }
    }

    private fun delete() {
        viewModelScope.launch {
            _uiState.value.imageUri?.let { uri ->
                val file = File(uri.path ?: "")
                if (file.exists()) {
                    file.delete() // Eliminar la imagen asociada si existe
                }
            }
            categoriaAeronaveRepository.deleteCategoriaAeronave(_uiState.value.toEntity())
            _uiState.update { it.copy(imageUri = null) } // Limpiar la imagen tras eliminar
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

fun CategoriaAeronaveUiState.toEntity(imagePath: String? = null) = CategoriaAeronaveEntity(
    categoriaId = categoriaId ?: 0,
    descripcionCategoria = descripcionCategoria ?: "",
    imagePath = imagePath ?: imageUri?.path
)