package edu.ucne.skyplanerent.presentation.categoriaaeronave

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
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
import androidx.core.content.edit
import androidx.core.net.toUri

@HiltViewModel
class CategoriaAeronaveViewModel @Inject constructor(
    private val categoriaAeronaveRepository: CategoriaAeronaveRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoriaAeronaveUiState())
    val uiState = _uiState.asStateFlow()

    init {
        initializeData()
        getCategorias()
    }

    // Maneja los eventos enviados desde la UI
    fun onEvent(event: CategoriaAeronaveEvent) {
        when (event) {
            is CategoriaAeronaveEvent.Delete -> delete()
            is CategoriaAeronaveEvent.New -> nuevo()
            is CategoriaAeronaveEvent.DescripcionCategoriaChange -> onDescripcionCategoriaChange(event.descripcionCategoria)
            is CategoriaAeronaveEvent.Save -> save()
            is CategoriaAeronaveEvent.CategoriaIdChange -> onCategoriaIdChange(event.categoriaId)
            is CategoriaAeronaveEvent.ImageSelected -> onImageSelected(event.uri)
            is CategoriaAeronaveEvent.ResetSuccess -> resetSuccess()
        }
    }

    // Inicializa datos predeterminados si la base de datos está vacía
    private fun initializeData() {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val isDataInitialized = prefs.getBoolean("data_initialized_categorias", false)
            if (!isDataInitialized && categoriaAeronaveRepository.getCount() == 0) {
                categoriaAeronaveRepository.saveCategoriaAeronave(
                    listOf(
                        CategoriaAeronaveEntity(
                            categoriaId = 1,
                            descripcionCategoria = "Monomotor a pistón",
                            imagePath = "https://www.oneair.es/wp-content/uploads/2024/05/aviones-monomotor-de-piston.jpg"
                        ),
                        CategoriaAeronaveEntity(
                            categoriaId = 2,
                            descripcionCategoria = "Bimotores a pistón",
                            imagePath = "https://www.great-flight.com/wp-content/uploads/2024/01/BIMOTOR-PISTON-great-flight.jpg"
                        ),
                        CategoriaAeronaveEntity(
                            categoriaId = 3,
                            descripcionCategoria = "Monomotor turboprop",
                            imagePath = "https://www.gacetaeronautica.com/gaceta/wp-101/wp-content/uploads/2021/08/TurbopropsIII-01.jpg"
                        ),
                        CategoriaAeronaveEntity(
                            categoriaId = 4,
                            descripcionCategoria = "Bimotores turboprop",
                            imagePath = "https://media.istockphoto.com/id/1139652369/es/foto/el-tr%C3%A1fico-en-el-aeropuerto-durante-el-atardecer.jpg?s=612x612&w=0&k=20&c=anj3wjdTjuHlVau6yUWz1L4HtNb1qw5eW24zqNO_6Mc="
                        ),
                        CategoriaAeronaveEntity(
                            categoriaId = 5,
                            descripcionCategoria = "Ejecutivos",
                            imagePath = "https://www.flyflapper.com/stories/wp-content/uploads/2021/12/18.jpg"
                        )
                    )
                )
                prefs.edit { putBoolean("data_initialized_categorias", true) }
            }
        }
    }

    // Obtiene todas las categorías desde el repositorio
    private fun getCategorias() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                categoriaAeronaveRepository.getAll().collect { categorias ->
                    _uiState.update {
                        it.copy(
                            categorias = categorias,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar categorías: ${e.message}"
                    )
                }
            }
        }
    }

    // Selecciona una categoría específica por ID
    fun selectedCategoria(categoriaId: Int) {
        viewModelScope.launch {
            if (categoriaId > 0) {
                try {
                    val categoria = categoriaAeronaveRepository.find(categoriaId)
                    _uiState.update {
                        it.copy(
                            categoriaId = categoria?.categoriaId,
                            descripcionCategoria = categoria?.descripcionCategoria.toString(),
                            imageUri = categoria?.imagePath?.toUri(),
                            isSuccess = false
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(errorMessage = "Error al cargar la categoría: ${e.message}")
                    }
                }
            }
        }
    }

    // Maneja el cambio en la descripción de la categoría
    private fun onDescripcionCategoriaChange(descripcionCategoria: String) {
        _uiState.update { it.copy(descripcionCategoria = descripcionCategoria, isSuccess = false) }
    }

    private fun onCategoriaIdChange(categoriaId: Int) {
        _uiState.update {
            it.copy(
                categoriaId = categoriaId,
                isSuccess = false
            )
        }
    }

    // Maneja la selección de una imagen
    private fun onImageSelected(uri: Uri) {
        _uiState.update { it.copy(imageUri = uri, isSuccess = false) }
    }

    // Guarda la imagen seleccionada en el almacenamiento interno
    private fun saveImage(context: Context, uri: Uri): String? {
        return try {
            val contentResolver = context.contentResolver
            val file = File(context.filesDir, "categoria_${System.currentTimeMillis()}.jpg")
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            // Generamos la URI con FileProvider
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file).toString()
        } catch (e: Exception) {
            // No actualizamos el estado aquí, retornamos null para que save() maneje el caso
            Log.e("CategoriaAeronaveViewModel", "Error al guardar la imagen: ${e.message}")
            null
        }
    }

    // Guarda una nueva categoría o actualiza una existente
    private fun save() {
        viewModelScope.launch {
            if (_uiState.value.descripcionCategoria.isBlank()) {
                _uiState.update { it.copy(errorMessage = "La descripción de la categoría es obligatoria") }
                return@launch
            }
            try {
                // Intentamos guardar la imagen, si falla usamos la URI original
                val imagePath = _uiState.value.imageUri?.let { uri ->
                    saveImage(context, uri) ?: uri.toString() // Usamos la URI original como respaldo
                }
                val categoria = _uiState.value.toEntity(imagePath)
                categoriaAeronaveRepository.saveCategoriaAeronave(categoria)
                _uiState.update {
                    it.copy(
                        errorMessage = null, // Limpiamos cualquier mensaje de error
                        imageUri = null,
                        isSuccess = true,
                        descripcionCategoria = "",
                        categoriaId = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al guardar la categoría: ${e.localizedMessage}") }
            }
        }
    }

    // Elimina la categoría seleccionada
    private fun delete() {
        viewModelScope.launch {
            try {
                _uiState.value.imageUri?.let { uri ->
                    val file = File(uri.path ?: "")
                    if (file.exists()) {
                        file.delete()
                    }
                }
                categoriaAeronaveRepository.deleteCategoriaAeronave(_uiState.value.toEntity())
                _uiState.update {
                    it.copy(
                        imageUri = null,
                        isSuccess = false,
                        categoriaId = null,
                        descripcionCategoria = "",
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al eliminar: ${e.message}") }
            }
        }
    }

    // Crea una nueva categoría (limpia los campos)
    private fun nuevo() {
        _uiState.update {
            it.copy(
                categoriaId = null,
                descripcionCategoria = "",
                errorMessage = null,
                imageUri = null,
                isSuccess = false
            )
        }
    }

    // Resetea el estado de éxito
    private fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}

// Convierte el estado de la UI a una entidad
fun CategoriaAeronaveUiState.toEntity(imagePath: String? = null) = CategoriaAeronaveEntity(
    categoriaId = categoriaId ?: 0,
    descripcionCategoria = descripcionCategoria,
    imagePath = imagePath ?: imageUri?.toString()
)