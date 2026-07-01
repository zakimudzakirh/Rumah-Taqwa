package com.rumahtaqwa.di

import android.content.Context
import androidx.room.Room
import com.rumahtaqwa.data.local.quran.AyatDao
import com.rumahtaqwa.data.local.quran.QuranDatabase
import com.rumahtaqwa.data.local.quran.SuratDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QuranDatabaseModule {

    @Provides
    @Singleton
    fun provideQuranDatabase(@ApplicationContext context: Context): QuranDatabase {
        return Room.databaseBuilder(
            context,
            QuranDatabase::class.java,
            "quran.db"
        )
        .createFromAsset("quran.db")
        .fallbackToDestructiveMigration(true)
        .build()
    }

    @Provides
    fun provideSurahDao(db: QuranDatabase): SuratDao = db.suratDao()

    @Provides
    fun provideAyahDao(db: QuranDatabase): AyatDao = db.ayatDao()
}