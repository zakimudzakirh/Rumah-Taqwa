package com.rumahtaqwa.data.local.reminder

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [IbadahReminderEntity::class], version = 1, exportSchema = true)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun ibadahReminderDao(): IbadahReminderDao
}