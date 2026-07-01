package com.rumahtaqwa.data.model

data class Ibadah(
    val id: String = "",
    val field: String = "",
    val label: String = "",
    val order: Int = 0,
    val type: String = "",
    val active: Boolean = false,
    val defaultTime: String = "",
    val unitName: String = "",
    val length: Int = 0,
)
