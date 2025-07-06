package edu.ucne.skyplanerent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName="TipoVuelo")
data class TipoVueloEntity(
    @PrimaryKey(autoGenerate = true)
    val vueloId:Int? = null,
    val rutaId: Int = 0,
    val tipoClienteId:Int = 0,
    val descripcionTipoVuelo:String = "",
    val precio:Double = 0.0,
)
