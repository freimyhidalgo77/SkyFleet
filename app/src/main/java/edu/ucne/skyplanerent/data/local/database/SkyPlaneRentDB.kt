package edu.ucne.skyplanerent.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ucne.skyplanerent.data.local.dao.ReservaDao
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity

@Database(
    entities = [
        ReservaEntity::class,
    ],
    version = 1,
    exportSchema = false
)

@TypeConverters(DateConverter::class)
abstract class SkyPlaneRentDB : RoomDatabase() {
    abstract fun reservaDao(): ReservaDao

}