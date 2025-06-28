package edu.ucne.skyplanerent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UsersAccounts")
data class UserAccount(
    @PrimaryKey(autoGenerate = true)
    var userId:Int? = null,
    var email:String = "",
    var passwword:String = "",
    var role:String = ""

)
