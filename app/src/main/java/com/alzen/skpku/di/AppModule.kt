package com.alzen.skpku.di

import android.content.Context
import androidx.room.Room
import com.alzen.skpku.BuildConfig
import com.alzen.skpku.PreferenceManager
import com.alzen.skpku.SkpRepository
import com.alzen.skpku.SupabaseApiService
import com.alzen.skpku.data.local.SkpDao
import com.alzen.skpku.data.local.SkpDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Hilt module to provide singleton dependencies across the application.
 * This centralizes the instantiation logic for our core data and network components.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = BuildConfig.SUPABASE_URL

    /**
     * Provides a singleton instance of [PreferenceManager].
     * Uses [@ApplicationContext] to avoid memory leaks.
     */
    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context: Context): PreferenceManager {
        return PreferenceManager(context)
    }

    /**
     * Provides a singleton [OkHttpClient] with an interceptor for Supabase authentication.
     * It dynamically retrieves the access token from [PreferenceManager].
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(preferenceManager: PreferenceManager): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
            
            // Retrieve token synchronously using runBlocking (Interceptors run on background threads)
            val token = runBlocking { preferenceManager.accessTokenFlow.first() }
            val authHeader = if (token.isNotEmpty()) "Bearer $token" else "Bearer ${BuildConfig.SUPABASE_ANON_KEY}"
            
            requestBuilder.addHeader("Authorization", authHeader)
            
            chain.proceed(requestBuilder.build())
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    /**
     * Provides a singleton instance of [SupabaseApiService].
     * Uses the provided [OkHttpClient] for network operations.
     */
    @Provides
    @Singleton
    fun provideSupabaseApiService(okHttpClient: OkHttpClient): SupabaseApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApiService::class.java)
    }

    /**
     * Provides a singleton instance of [SkpDatabase].
     * Configures the Room database with the application context.
     */
    @Provides
    @Singleton
    fun provideSkpDatabase(@ApplicationContext context: Context): SkpDatabase {
        return Room.databaseBuilder(
            context,
            SkpDatabase::class.java,
            "skp_database"
        ).build()
    }

    /**
     * Provides the [SkpDao] from the [SkpDatabase].
     */
    @Provides
    fun provideSkpDao(database: SkpDatabase): SkpDao {
        return database.skpDao()
    }

    /**
     * Provides a singleton instance of [SkpRepository].
     * Injects both [SupabaseApiService] and [SkpDao] for offline-first support.
     */
    @Provides
    @Singleton
    fun provideSkpRepository(
        apiService: SupabaseApiService,
        skpDao: SkpDao
    ): SkpRepository {
        return SkpRepository(apiService, skpDao)
    }
}
