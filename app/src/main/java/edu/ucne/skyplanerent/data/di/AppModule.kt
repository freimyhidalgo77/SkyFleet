package edu.ucne.skyplanerent.data.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.skyplanerent.SkyPlaneRent
import edu.ucne.skyplanerent.data.local.database.SkyPlaneRentDB
import edu.ucne.skyplanerent.presentation.login.SessionManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext applicationContext: Context): SkyPlaneRentDB {
        return Room.databaseBuilder(
            applicationContext,
            SkyPlaneRentDB::class.java,
            "planeDb"
        ).fallbackToDestructiveMigration().build()
    }

    //Provide de Firebase
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun provideProfileAuth(appDataDb: SkyPlaneRentDB)= appDataDb.userDao()

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    @Singleton
    fun provideReservaDb(appDataDb: SkyPlaneRentDB) = appDataDb.reservaDao()

    @Provides
    @Singleton
    fun provideRutaDb(appDataDb: SkyPlaneRentDB) = appDataDb.rutaDao()

    @Provides
    @Singleton
    fun provideTipoVieloDb(appDataDb: SkyPlaneRentDB) = appDataDb.tipoVueloDao()

    @Provides
    @Singleton
    fun provideFormularioDb(appDataDb: SkyPlaneRentDB) = appDataDb.formularioDao()

    @Provides
    @Singleton
    fun provideAeronaveDb(appDataDb: SkyPlaneRentDB) = appDataDb.aeronaveDao()

    @Provides
    @Singleton
    fun provideCategoriaAeronaveDb(appDataDb: SkyPlaneRentDB) = appDataDb.categoriaAeronaveDao()

    @Provides
    @Singleton
    fun provideAdminDb(appDataDb: SkyPlaneRentDB) = appDataDb.adminDao()

}