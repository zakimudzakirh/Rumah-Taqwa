package com.rumahtaqwa.data.model.quran

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ayah",
    foreignKeys = [ForeignKey(
        entity = Surat::class,
        parentColumns = ["id"],
        childColumns = ["surah_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("surah_id"), Index("juz")]
)
data class Ayat(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "surah_id") val surahId: Int,
    @ColumnInfo(name = "ayah_number") val ayahNumber: Int,
    val arabic: String,
    val translation: String,
    val juz: Int
)