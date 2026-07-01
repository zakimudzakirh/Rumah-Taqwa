package com.rumahtaqwa.di

import android.content.Context
import androidx.room.Room
import com.rumahtaqwa.data.local.reminder.IbadahReminderDao
import com.rumahtaqwa.data.local.reminder.ReminderDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReminderDatabaseModule {

    @Provides
    @Singleton
    fun provideReminderDatabase(@ApplicationContext context: Context): ReminderDatabase =
        Room.databaseBuilder(context, ReminderDatabase::class.java, "reminder_db").build()

    @Provides
    fun provideIbadahReminderDao(db: ReminderDatabase): IbadahReminderDao = db.ibadahReminderDao()
}