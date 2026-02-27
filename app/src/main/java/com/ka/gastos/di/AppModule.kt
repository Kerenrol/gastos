package com.ka.gastos.di

import com.ka.gastos.features.data.remote.ApiService
import com.ka.gastos.features.data.remote.WebSocketManager
import com.ka.gastos.features.gastos.data.repository.GastoRepositoryImpl
import com.ka.gastos.features.gastos.domain.repository.GastoRepository
import com.ka.gastos.features.grupos.data.repository.GrupoRepositoryImpl
import com.ka.gastos.features.grupos.domain.repository.GrupoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGrupoRepository(apiService: ApiService): GrupoRepository {
        return GrupoRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideGastoRepository(apiService: ApiService, webSocketManager: WebSocketManager): GastoRepository {
        return GastoRepositoryImpl(apiService, webSocketManager)
    }

}
