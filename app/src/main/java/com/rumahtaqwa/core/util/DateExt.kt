package com.rumahtaqwa.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.toFormattedString(): String {
    val sdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.forLanguageTag("id-ID"))
    return sdf.format(this)
}

fun Date.toDayLabel(): String {
    val sdf = SimpleDateFormat("EEE", Locale.forLanguageTag("id-ID"))
    return sdf.format(this) // "Sen", "Sel", "Rab", dst
}

fun Date.toDayNumber2Digits(): String {
    val sdf = SimpleDateFormat("dd", Locale.forLanguageTag("id-ID"))
    return sdf.format(this) // "16", "17", "18", dst
}

fun Date.toDayNumber(): String {
    val sdf = SimpleDateFormat("d", Locale.forLanguageTag("id-ID"))
    return sdf.format(this) // "16", "17", "18", dst
}

fun Date.toFormatDataString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.forLanguageTag("id-ID"))
    return sdf.format(this)
}

fun Date.toMonthLabel(): String {
    val sdf = SimpleDateFormat("MMM yyyy", Locale.forLanguageTag("id-ID"))
    return sdf.format(this)
}