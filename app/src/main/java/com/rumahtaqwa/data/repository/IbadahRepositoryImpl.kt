package com.rumahtaqwa.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.rumahtaqwa.data.model.Ibadah
import com.rumahtaqwa.data.model.IbadahSetting
import com.rumahtaqwa.domain.repository.IbadahRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class IbadahRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : IbadahRepository {

    private val settingsCollection
        get() = firestore
            .collection("users")
            .document(auth.currentUser!!.uid)
            .collection("ibadahSettings")

    private val logsCollection
        get() = firestore
            .collection("users")
            .document(auth.currentUser!!.uid)
            .collection("logs")

    override fun getIbadah(): Flow<List<Ibadah>> = callbackFlow {
        val listener = firestore
            .collection("ibadah")
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val ibadah = snapshot?.documents
                    ?.mapNotNull { it.toObject(Ibadah::class.java)?.copy(id = it.id) }
                    ?: emptyList()
                trySend(ibadah)
            }
        awaitClose { listener.remove() }
    }

    override fun getIbadahSettings(): Flow<Map<String, IbadahSetting>?> = callbackFlow {
        val listener = settingsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val settings = snapshot?.documents?.associate { doc ->
                    doc.id to doc.toObject(IbadahSetting::class.java)!!
                }
                trySend(settings)
            }
        awaitClose { listener.remove() }
    }

    override fun getListenerLogs(startDate: String, endDate: String): Flow<List<Map<String, Any>>?> = callbackFlow {
        val listener = logsCollection
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            .addSnapshotListener { snapshot, error ->
                val logs = snapshot?.documents?.map { doc ->
                    doc.data ?: emptyMap()
                }
                trySend(logs)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getLogs(startDate: String, endDate: String): List<Map<String, Any>>? {
        return logsCollection
            .whereGreaterThanOrEqualTo(FieldPath.documentId(), startDate)
            .whereLessThanOrEqualTo(FieldPath.documentId(), endDate)
            .get()
            .await()
            .documents
            .map { doc ->
                (doc.data ?: emptyMap()) + mapOf("date" to doc.id)
            }
    }

    override suspend fun updateSettings(settings: Map<String, IbadahSetting>) {
        val batch = firestore.batch()
        settings.forEach { (id, setting) ->
            val ref = settingsCollection.document(id)
            batch.set(ref, setting.copy(id = id))
        }
        batch.commit().await()
    }

    override suspend fun saveIbadahByDate(date: String, field: String, value: String) {
        val data = mapOf(
            field to value
        )
        logsCollection.document(date).set(data, SetOptions.merge()).await()
    }

    override suspend fun saveLogsForMonth(
        data: Map<String, Map<String, String>>,
        previousData: Map<String, Map<String, String>> // untuk tahu field apa yang perlu di-delete
    ) {
        val batch = firestore.batch()
        var hasWrites = false

        data.forEach { (dateStr, fields) ->
            val allKeys = fields.keys + (previousData[dateStr]?.keys ?: emptySet())
            val payload = mutableMapOf<String, Any>()

            allKeys.forEach { key ->
                val value = fields[key]
                if (!value.isNullOrBlank()) {
                    payload[key] = value
                } else if (previousData[dateStr]?.get(key)?.isNotBlank() == true) {
                    payload[key] = FieldValue.delete()
                }
            }

            if (payload.isNotEmpty()) {
                batch.set(logsCollection.document(dateStr), payload, SetOptions.merge())
                hasWrites = true
            }
        }

        if (hasWrites) batch.commit().await()
    }

}