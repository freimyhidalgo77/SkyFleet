package edu.ucne.skyplanerent.presentation.ruta_y_viajes.formulario

import edu.ucne.skyplanerent.data.local.entity.FormularioEntity
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
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
    val formularios:List<FormularioEntity> = emptyList()
)