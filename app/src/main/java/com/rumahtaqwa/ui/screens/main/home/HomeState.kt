package com.rumahtaqwa.ui.screens.main.home

import com.google.firebase.auth.FirebaseUser
import com.rumahtaqwa.data.model.Ibadah
import com.rumahtaqwa.data.model.IbadahSetting
import com.rumahtaqwa.data.model.quran.Surat

data class HomeState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val user: FirebaseUser? = null,
    val ibadah: List<Ibadah> = emptyList(),
    val settings: Map<String, IbadahSetting>? = emptyMap(),
    val surats: List<Surat> = emptyList(),
    val weekly: Map<String, Int>? = emptyMap(),
    val weeklyCount: Int = 0,
    val weeklyTotal: Int = 1,
    val weeklyProgress: Float = 0f,
    val today: Map<String, String> = emptyMap()
)