package edu.ucne.skyplanerent.presentation.categoriaaeronave

sealed interface CategoriaAeronaveEvent {
    data class CategoriaIdChange(val categoriaId: Int): CategoriaAeronaveEvent
    data class DescripcionCategoriaChange(val descripcionCategoria: String): CategoriaAeronaveEvent
    data object Save: CategoriaAeronaveEvent
    data object Delete: CategoriaAeronaveEvent
    data object New: CategoriaAeronaveEvent
}