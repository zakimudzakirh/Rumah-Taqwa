package com.rumahtaqwa.ui.screens.main.rekap

import com.rumahtaqwa.data.model.Ibadah
import com.rumahtaqwa.data.model.IbadahSetting
import com.rumahtaqwa.data.model.quran.Surat
import java.util.Date

data class RekapState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val date: Date = Date(),
    val ibadah: List<Ibadah> = emptyList(),
    val settings: Map<String, IbadahSetting>? = emptyMap(),
    val surats: List<Surat> = emptyList(),
    val data: Map<String, Map<String, String>> = emptyMap(),
    val originalData: Map<String, Map<String, String>> = emptyMap(),
    val dates: List<Date> = emptyList(),
    val logs: Map<String, String> = emptyMap()
)