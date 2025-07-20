package edu.ucne.skyplanerent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName="TipoVuelo")
data class TipoVueloEntity(
    @PrimaryKey
    val vueloId:Int? = null,
    val nombreVuelo: String = "",
    val descripcionTipoVuelo:String = "",
    val isPendingSync: Boolean = false // Nuevo campo
)
