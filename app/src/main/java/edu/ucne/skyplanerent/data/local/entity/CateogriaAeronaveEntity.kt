package edu.ucne.skyplanerent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categoria_aeronave")
data class CategoriaAeronaveEntity(
    @PrimaryKey(autoGenerate = true)
    val categoriaId: Int = 0,
    val descripcionCategoria: String,
)