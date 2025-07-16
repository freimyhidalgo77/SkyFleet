package edu.ucne.skyplanerent.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.ucne.skyplanerent.data.local.dao.AeronaveDao
import edu.ucne.skyplanerent.data.local.dao.CategoriaAeronaveDao
import edu.ucne.skyplanerent.data.local.dao.FormularioDao
import edu.ucne.skyplanerent.data.local.dao.ReservaDao
import edu.ucne.skyplanerent.data.local.dao.RutaDao
import edu.ucne.skyplanerent.data.local.dao.TipoVueloDao
import edu.ucne.skyplanerent.data.local.entity.AeronaveEntity
import edu.ucne.skyplanerent.data.local.entity.CategoriaAeronaveEntity
import edu.ucne.skyplanerent.data.local.entity.FormularioEntity
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import edu.ucne.skyplanerent.data.local.entity.RutaEntity
import edu.ucne.skyplanerent.data.local.entity.TipoVueloEntity

@Database(
    entities = [
        ReservaEntity::class,
        RutaEntity::class,
        TipoVueloEntity::class,
        FormularioEntity::class,
        AeronaveEntity::class,
        CategoriaAeronaveEntity::class,
    ],
    version = 4,
    exportSchema = false
)

@TypeConverters(DateConverter::class)
abstract class SkyPlaneRentDB : RoomDatabase() {
    abstract fun reservaDao(): ReservaDao
    abstract fun rutaDao(): RutaDao
    abstract fun tipoVueloDao(): TipoVueloDao
    abstract fun formularioDao():FormularioDao
    abstract fun aeronaveDao():AeronaveDao
    abstract fun categoriaAeronaveDao(): CategoriaAeronaveDao

}