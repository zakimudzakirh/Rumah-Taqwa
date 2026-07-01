package com.rumahtaqwa.data.model

data class IbadahSetting(
    val id: String = "",
    val field: String = "",
    val recap: Boolean = true,
    val perWeek: Int = 1,
//    val notify: Boolean = false,
//    val notifTime: String = "00:00"
)