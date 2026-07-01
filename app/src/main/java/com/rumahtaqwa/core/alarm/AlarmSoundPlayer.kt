package com.rumahtaqwa.core.alarm

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.net.toUri

object AlarmSoundPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context, soundUri: String?) {
        stop()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            try {
                setDataSource(context, soundUri?.toUri() ?: defaultAlarmUri(context))
                prepare()
            } catch (e: Exception) {
                reset()
                setDataSource(context, defaultAlarmUri(context))
                prepare()
            }
            isLooping = true
            start()
        }
    }

    private fun defaultAlarmUri(context: Context): Uri =
        RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getValidRingtoneUri(context)

    fun stop() {
        mediaPlayer?.apply { runCatching { stop() }; release() }
        mediaPlayer = null
    }
}