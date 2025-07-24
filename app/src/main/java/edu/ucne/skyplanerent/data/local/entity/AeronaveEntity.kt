package edu.ucne.skyplanerent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Aeronaves")
data class AeronaveEntity(
    @PrimaryKey
    val AeronaveId:Int? = null,
    val estadoId:Int? = null,
    val ModeloAvion:String,
    val DescripcionCategoria:String,
    val Registracion:String,
    val CostoXHora:Double?,
    val DescripcionAeronave:String,
    val VelocidadMaxima:Double?,
    val DescripcionMotor:String,
    val CapacidadCombustible:Int = 0,
    val ConsumoXHora:Int = 0,
    val Peso:Double?,
    val Rango:Int = 0 ,
    val CapacidadPasajeros:Int = 0,
    val AltitudMaxima:Int  = 0,
    val Licencia:String,
    val isPendingSync: Boolean = false, // Nuevo campo
    val imagePath: String? = null
)