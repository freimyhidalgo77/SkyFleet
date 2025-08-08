package edu.ucne.skyplanerent.presentation.rutayviajes.formulario

import edu.ucne.skyplanerent.data.local.entity.FormularioEntity
import java.util.Date

data class FormularioUiState (
    val formularioId: Int? = null,
    val nombre:String = "",
    val apellido:String = "",
    val correo:String = "",
    val telefono:String = "",
    val pasaporte:String = "",
    val ciudadResidencia:String = "",
    val successMessage:String? = "",
    val errorMessage:String? = "",
    val cantidadPasajeros:Int = 0,
    val formularios:List<FormularioEntity> = emptyList(),
    val nacimiento:Date? = null,
    val isEditing: Boolean = false
)