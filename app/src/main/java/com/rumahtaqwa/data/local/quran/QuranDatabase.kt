package com.rumahtaqwa.data.local.quran

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rumahtaqwa.data.model.quran.Ayat
import com.rumahtaqwa.data.model.quran.Surat

@Database(
    entities = [Surat::class, Ayat::class],
    version = 1,
    exportSchema = true
)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun suratDao(): SuratDao
    abstract fun ayatDao(): AyatDao
}