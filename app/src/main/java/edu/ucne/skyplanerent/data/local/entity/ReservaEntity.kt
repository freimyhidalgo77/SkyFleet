package edu.ucne.skyplanerent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName="Reservas")
data class ReservaEntity(
    @PrimaryKey(autoGenerate = true)
    val reservaId:Int? = null,
    val estadoId:Int? = null,
    val formularioId:Int? = null,
    val metodoPagoId:Int? = null,
    val tipoVueloId:Int? = null,
    val categoriaId:Int? = null,
    val rutaId:Int? = null,
    val fecha: Date?,
    val impuesto: Double,
    val tarifa:Double

)