package com.rumahtaqwa.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")

@Singleton
class SettingsPreferenceManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private object Keys {
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
    }

    val soundEnabled: Flow<Boolean> = context.settingsDataStore.data
        .map { prefs -> prefs[Keys.SOUND_ENABLED] ?: true }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.SOUND_ENABLED] = enabled
        }
    }
}
