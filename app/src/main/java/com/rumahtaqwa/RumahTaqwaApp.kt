package com.rumahtaqwa

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.rumahtaqwa.core.alarm.AlarmNotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RumahTaqwaApp: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        configureFirestore()
        AlarmNotificationHelper.createChannel(this)
    }

    private fun configureFirestore() {
        val settings = firestoreSettings {
            setLocalCacheSettings(persistentCacheSettings {
                setSizeBytes(
                    FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
                )
            })
        }
        Firebase.firestore.firestoreSettings = settings
    }
}