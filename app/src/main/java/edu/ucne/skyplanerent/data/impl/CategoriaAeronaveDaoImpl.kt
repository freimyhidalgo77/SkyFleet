package edu.ucne.skyplanerent.data.impl

import com.google.firebase.firestore.FirebaseFirestore
import edu.ucne.skyplanerent.data.local.dao.CategoriaAeronaveDao
import edu.ucne.skyplanerent.data.local.entity.CategoriaAeronaveEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

abstract class CategoriaAeronaveDaoImpl(
    private val firestore: FirebaseFirestore
) : CategoriaAeronaveDao {
    private val collection = firestore.collection("categorias_aeronave")

    override suspend fun saveCategoriaAeronave(categoriaAeronaves: List<CategoriaAeronaveEntity>) {
        categoriaAeronaves.forEach { categoria ->
            collection.document(categoria.categoriaId.toString()).set(categoria).await()
        }
    }

    override suspend fun find(id: Int): CategoriaAeronaveEntity? {
        val snapshot = collection.document(id.toString()).get().await()
        return snapshot.toObject(CategoriaAeronaveEntity::class.java)
    }

    override suspend fun deleteCategoriaAeronave(categoriaAeronave: CategoriaAeronaveEntity) {
        collection.document(categoriaAeronave.categoriaId.toString()).delete().await()
    }

    override fun getAll(): Flow<List<CategoriaAeronaveEntity>> = flow {
        val snapshot = collection.get().await()
        val categorias = snapshot.toObjects(CategoriaAeronaveEntity::class.java)
        emit(categorias)
    }

    override suspend fun getCount(): Int {
        val snapshot = collection.get().await()
        return snapshot.size()
    }
}