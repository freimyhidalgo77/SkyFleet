package edu.ucne.skyplanerent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="Rutas")
data class RutaEntity(
    @PrimaryKey(autoGenerate = true)
    val rutaId:Int? = null,
    val origen:String = "",
    val destino:String = "",
    val distancia:Double? = 0.0,
    val duracion:Int = 0,
    )
