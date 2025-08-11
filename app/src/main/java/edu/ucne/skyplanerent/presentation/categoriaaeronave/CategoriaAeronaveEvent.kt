package edu.ucne.skyplanerent.presentation.categoriaaeronave

import android.net.Uri

sealed interface CategoriaAeronaveEvent {
    data class CategoriaIdChange(val categoriaId: Int) : CategoriaAeronaveEvent
    data class DescripcionCategoriaChange(val descripcionCategoria: String) : CategoriaAeronaveEvent
    data object Save : CategoriaAeronaveEvent
    data object Delete : CategoriaAeronaveEvent
    data object New : CategoriaAeronaveEvent
    data class ImageSelected(val uri: Uri) : CategoriaAeronaveEvent
    data object ResetSuccess : CategoriaAeronaveEvent
}