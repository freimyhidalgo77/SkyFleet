package edu.ucne.skyplanerent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Rutas")
data class RutaEntity(
    @PrimaryKey val rutaId: Int? = null, // Sin autoGenerate
    val origen: String = "",
    val destino: String = "",
    val distancia: Double? = 0.0,
    val duracion: Int = 0,
    val isPendingSync: Boolean = false
)