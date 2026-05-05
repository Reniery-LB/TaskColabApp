package com.taskcolab.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Extensión para crear el DataStore (sesión)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "taskcolab_prefs"
)

/**
 * Módulo de Hilt para proveer dependencias globales.
 *
 * Aquí se registran:
 * - DataStore (sesión del usuario)
 * - Retrofit (cuando el backend esté listo)
 * - Room Database (cache local, cuando se implemente)
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provee el DataStore para guardar la sesión del usuario.
     * Singleton: solo existe una instancia en toda la app.
     */
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.dataStore

    // TODO: Agregar Retrofit cuando el backend esté listo
    // @Provides
    // @Singleton
    // fun provideRetrofit(): Retrofit {
    //     return Retrofit.Builder()
    //         .baseUrl("http://10.0.2.2/taskcolab/")  // localhost en emulador
    //         .addConverterFactory(GsonConverterFactory.create())
    //         .build()
    // }
}
