package com.assignment.myportfolio.di

import com.assignment.myportfolio.data.remote.PortfolioService
import com.assignment.myportfolio.data.remote.PortfolioServiceFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val BASE_URL = "https://35dee773a9ec441e9f38d5fc249406ce.api.mockbin.io"
    
    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()
    
    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    
    @Provides
    @Singleton
    fun providePortfolioService(retrofit: Retrofit): PortfolioService =
        PortfolioServiceFactory(retrofit).getPortfolioService()
}
