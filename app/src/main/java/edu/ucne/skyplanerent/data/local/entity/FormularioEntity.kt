package edu.ucne.skyplanerent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Formularios")
data class FormularioEntity(
    @PrimaryKey(autoGenerate = true)
    val formularioId:Int? = null,
    val nombre:String = "",
    val apellido:String = "",
    val correo: String = "",
    val telefono:String = "",
    val pasaporte:String = "",
    val ciudadResidencia:String = ""
)
