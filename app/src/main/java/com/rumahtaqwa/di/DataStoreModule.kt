package com.rumahtaqwa.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    // Kosong — ThemePreferenceManager udah pakai @Inject constructor + @Singleton,
    // Hilt otomatis tau cara provide-nya tanpa perlu @Provides manual.
}