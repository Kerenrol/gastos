package com.ka.gastos.features.login.di

import com.ka.gastos.features.login.data.repositories.LoginRepositoryImpl
import com.ka.gastos.features.login.domain.repositories.LoginRepository
import com.ka.gastos.features.login.domain.usecases.LoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Provides
    fun provideLoginRepository(): LoginRepository {
        return LoginRepositoryImpl()
    }

    @Provides
    fun provideLoginUseCase(repository: LoginRepository): LoginUseCase {
        return LoginUseCase(repository)
    }
}