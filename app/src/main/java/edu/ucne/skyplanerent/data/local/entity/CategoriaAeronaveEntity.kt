package edu.ucne.skyplanerent.data.local.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categoria_aeronave")
data class CategoriaAeronaveEntity(
    @PrimaryKey(autoGenerate = true)
    val categoriaId: Int = 0,
    val descripcionCategoria: String,
    val imagePath: String? = null
)