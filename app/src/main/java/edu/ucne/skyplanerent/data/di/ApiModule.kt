package edu.ucne.skyplanerent.data.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.ucne.skyplanerent.data.remote.aeronaves.AeronavesManagerApi
import edu.ucne.skyplanerent.data.remote.dto.TipoVueloDTO
import edu.ucne.skyplanerent.data.remote.rutas.RutaManagerApi
import edu.ucne.skyplanerent.data.remote.tiposVuelos.TipoVueloManagerApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    private const val BASE_URL_SkyFleetApi = "http://skyfleetapi.somee.com/"

    @Provides
    @Singleton
    fun providesMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun providesAeronaveManagerApi(moshi: Moshi): AeronavesManagerApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_SkyFleetApi)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AeronavesManagerApi::class.java)

    }

    @Provides
    @Singleton
    fun providesRutaManagerApi(moshi: Moshi): RutaManagerApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_SkyFleetApi)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RutaManagerApi::class.java)


    }

    @Provides
    @Singleton
    fun providesTipoVueloManagerApi(moshi: Moshi): TipoVueloManagerApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_SkyFleetApi)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TipoVueloManagerApi::class.java)


    }

}
