package com.rumahtaqwa.data.repository

import com.rumahtaqwa.data.local.quran.AyatDao
import com.rumahtaqwa.data.local.quran.SuratDao
import com.rumahtaqwa.data.model.quran.Ayat
import com.rumahtaqwa.data.model.quran.Surat
import com.rumahtaqwa.domain.repository.QuranRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuranRepositoryImpl @Inject constructor(
    private val surahDao: SuratDao,
    private val ayahDao: AyatDao
) : QuranRepository {
    override fun getAllSurah(): Flow<List<Surat>> = surahDao.getAllSurah()
    override fun getAyahBySurah(surahId: Int): Flow<List<Ayat>> = ayahDao.getAyahBySurah(surahId)
    override fun getAyahByJuz(juz: Int): Flow<List<Ayat>> = ayahDao.getAyahByJuz(juz)
    override fun searchAyah(keyword: String): Flow<List<Ayat>> = ayahDao.searchAyah(keyword)
}