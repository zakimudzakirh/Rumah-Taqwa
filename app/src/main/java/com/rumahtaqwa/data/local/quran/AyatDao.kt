package com.rumahtaqwa.data.local.quran

import androidx.room.Dao
import androidx.room.Query
import com.rumahtaqwa.data.model.quran.Ayat
import kotlinx.coroutines.flow.Flow

@Dao
interface AyatDao {
    @Query("SELECT * FROM ayah WHERE surah_id = :surahId ORDER BY ayah_number ASC")
    fun getAyahBySurah(surahId: Int): Flow<List<Ayat>>

    @Query("SELECT * FROM ayah WHERE juz = :juz ORDER BY id ASC")
    fun getAyahByJuz(juz: Int): Flow<List<Ayat>>

    @Query("SELECT * FROM ayah WHERE arabic LIKE '%' || :keyword || '%' OR translation LIKE '%' || :keyword || '%'")
    fun searchAyah(keyword: String): Flow<List<Ayat>>
}