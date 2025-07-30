package edu.ucne.skyplanerent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "UsersAccounts")
data class UserRegisterAccount(
    @PrimaryKey(autoGenerate = true)
    var userRegisterId:Int? = null,
    var nombre:String = "",
    var apellido:String = "",
    var correo:String = "",
    var telefono:String = "",
    var contrasena:String = "",
    var direcccion:String = "",
    var fecha: Date?,
    val imagePath: String? = null

)
