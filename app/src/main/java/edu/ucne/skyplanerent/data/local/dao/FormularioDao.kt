package edu.ucne.skyplanerent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.skyplanerent.data.local.entity.FormularioEntity
import edu.ucne.skyplanerent.data.local.entity.ReservaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FormularioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(formulario: FormularioEntity): Long


    @Query("SELECT * FROM Formularios WHERE formularioId = :id LIMIT 1")
    suspend fun find(id: Int): FormularioEntity

    @Delete
    suspend fun delete(formulario: FormularioEntity)

    @Query("SELECT * FROM Formularios")
    fun getAll(): Flow<List<FormularioEntity>>

}