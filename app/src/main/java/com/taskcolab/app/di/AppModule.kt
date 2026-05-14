package com.taskcolab.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.taskcolab.app.BuildConfig
import com.taskcolab.app.data.remote.TaskColabApi
import com.taskcolab.app.data.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @Provides
    @Singleton
    fun provideOkHttpClient(
        sessionManager: SessionManager
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = runBlocking { sessionManager.getToken() }
                val request = chain.request().newBuilder().apply {
                    if (!token.isNullOrBlank()) {
                        addHeader("Authorization", "Bearer $token")
                    }
                }.build()

                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.TASKCOLAB_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTaskColabApi(
        retrofit: Retrofit
    ): TaskColabApi = retrofit.create(TaskColabApi::class.java)
}
