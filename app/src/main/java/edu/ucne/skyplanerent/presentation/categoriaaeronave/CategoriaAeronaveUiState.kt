package edu.ucne.skyplanerent.presentation.categoriaaeronave

import android.net.Uri
import edu.ucne.skyplanerent.data.local.entity.CategoriaAeronaveEntity

data class CategoriaAeronaveUiState(
    val categoriaId: Int? = null,
    val descripcionCategoria: String = "",
    val errorMessage: String? = null,
    val categorias: List<CategoriaAeronaveEntity> = emptyList(),
    val imageUri: Uri? = null // Nuevo campo para la imagen
)
