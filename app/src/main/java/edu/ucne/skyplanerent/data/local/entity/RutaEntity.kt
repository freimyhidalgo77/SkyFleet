package edu.ucne.skyplanerent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="Rutas")
data class RutaEntity(
    @PrimaryKey(autoGenerate = true)
    val rutaId:Int? = null,
    val aeronaveId: Int = 0,
    val origen:String = "",
    val destino:String = "",
    val distancia:String = "",
    val duracionEstimada:String = "",

    )
