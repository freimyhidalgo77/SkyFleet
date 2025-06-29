package edu.ucne.skyplanerent.data.local.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.skyplanerent.SkyPlaneRent
import edu.ucne.skyplanerent.data.local.database.SkyPlaneRentDB
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

    @Provides
    @Singleton
    fun provideReservaDb(appDataDb: SkyPlaneRentDB) = appDataDb.reservaDao()


}