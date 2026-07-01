package com.rumahtaqwa.data.local.reminder

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface IbadahReminderDao {
    @Upsert
    suspend fun upsert(entity: IbadahReminderEntity)

    @Query("SELECT * FROM ibadah_reminder WHERE ibadahId = :id")
    suspend fun getById(id: String): IbadahReminderEntity?

    @Query("SELECT * FROM ibadah_reminder WHERE ibadahId = :id")
    fun observeById(id: String): Flow<IbadahReminderEntity?>

    @Query("SELECT * FROM ibadah_reminder")
    fun observeAll(): Flow<List<IbadahReminderEntity>>

    @Query("SELECT * FROM ibadah_reminder WHERE notify = 1")
    suspend fun getAllEnabled(): List<IbadahReminderEntity>
}