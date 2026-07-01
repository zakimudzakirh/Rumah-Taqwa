package com.rumahtaqwa.di

import com.rumahtaqwa.data.repository.AuthRepositoryImpl
import com.rumahtaqwa.data.repository.IbadahRepositoryImpl
import com.rumahtaqwa.data.repository.QuranRepositoryImpl
import com.rumahtaqwa.data.repository.ReminderRepositoryImpl
import com.rumahtaqwa.domain.repository.AuthRepository
import com.rumahtaqwa.domain.repository.IbadahRepository
import com.rumahtaqwa.domain.repository.QuranRepository
import com.rumahtaqwa.domain.repository.ReminderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindIbadahrepository(
        impl: IbadahRepositoryImpl
    ): IbadahRepository

    @Binds
    @Singleton
    abstract fun bindQuranRepository(
        impl: QuranRepositoryImpl
    ): QuranRepository

    @Binds
    @Singleton
    abstract fun bindReminderRepository(
        impl: ReminderRepositoryImpl
    ): ReminderRepository

}