package edu.ucne.skyplanerent.data.local.entity

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="Admin")
data class AdminEntity(
    @PrimaryKey
    val adminId:Int? = null,
    val correo: String = "",
    val contrasena:String = "",
    val foto:String = "",
    val isPendingSync: Boolean = false // Nuevo campo
)

