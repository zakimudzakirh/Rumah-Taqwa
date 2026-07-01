package com.rumahtaqwa.data.local.quran

import androidx.room.Dao
import androidx.room.Query
import com.rumahtaqwa.data.model.quran.Surat
import kotlinx.coroutines.flow.Flow

@Dao
interface SuratDao {
    @Query("SELECT * FROM surah ORDER BY id ASC")
    fun getAllSurah(): Flow<List<Surat>>

    @Query("SELECT * FROM surah WHERE id = :surahId")
    suspend fun getSurahById(surahId: Int): Surat?
}