package com.rumahtaqwa.domain.usecase

import com.rumahtaqwa.data.model.quran.Surat
import com.rumahtaqwa.domain.repository.QuranRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuranUseCase @Inject constructor(
    private val quranRepository: QuranRepository
) {
    fun getAllSurat(): Flow<List<Surat>> = quranRepository.getAllSurah()
}