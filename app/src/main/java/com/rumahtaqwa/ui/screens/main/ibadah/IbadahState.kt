package com.rumahtaqwa.ui.screens.main.ibadah

import com.rumahtaqwa.data.local.reminder.IbadahReminderEntity
import com.rumahtaqwa.data.model.Ibadah
import com.rumahtaqwa.data.model.IbadahSetting

data class IbadahState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val ibadah: List<Ibadah> = emptyList(),
    val settings: Map<String, IbadahSetting>? = null,
    val reminders: Map<String, IbadahReminderEntity> = emptyMap()
)

enum class IbadahField {
    PERWEEK, ISRECAP
//    ISNOTIFY, NOTIFYTIME
}