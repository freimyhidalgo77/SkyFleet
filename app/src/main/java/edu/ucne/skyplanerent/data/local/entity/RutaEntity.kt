package edu.ucne.skyplanerent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName="Rutas")

data class RutaEntity(
    @PrimaryKey(autoGenerate = true)
    val rutaId:Int? = null,
    val aeronaveId:Int? = null,
    val origen:String? = null,
    val destino:String? = null,
    val distancia:String? = null,
    val duracionEstimada:String? = null,

)
