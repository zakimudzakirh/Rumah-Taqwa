package com.rumahtaqwa.data.model.quran

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surah")
data class Surat(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name_latin") val nameLatin: String,
    @ColumnInfo(name = "name_arabic") val nameArabic: String,
    @ColumnInfo(name = "name_id") val nameId: String,
    val type: String,
    @ColumnInfo(name = "total_ayah") val totalAyah: Int,
    @ColumnInfo(name = "page_start") val pageStart: Int
)