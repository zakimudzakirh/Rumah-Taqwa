package com.rumahtaqwa.domain.repository

import com.rumahtaqwa.data.model.quran.Ayat
import com.rumahtaqwa.data.model.quran.Surat
import kotlinx.coroutines.flow.Flow

interface QuranRepository {
    fun getAllSurah(): Flow<List<Surat>>
    fun getAyahBySurah(surahId: Int): Flow<List<Ayat>>
    fun getAyahByJuz(juz: Int): Flow<List<Ayat>>
    fun searchAyah(keyword: String): Flow<List<Ayat>>
}