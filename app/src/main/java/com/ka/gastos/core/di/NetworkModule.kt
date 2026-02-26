package com.ka.gastos.core.di

import com.ka.gastos.features.data.remote.GastosApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @RickAndMortyRetrofit
    fun provideRickAndMortyRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGastosApi(@RickAndMortyRetrofit retrofit: Retrofit): GastosApi {
        return retrofit.create(GastosApi::class.java)
    }
}
