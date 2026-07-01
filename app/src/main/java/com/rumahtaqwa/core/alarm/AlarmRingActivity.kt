package com.rumahtaqwa.core.alarm

import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rumahtaqwa.ui.theme.RumahTaqwaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmRingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
        getSystemService(KeyguardManager::class.java).requestDismissKeyguard(this, null)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val ibadahId = intent.getStringExtra(AlarmScheduler.EXTRA_IBADAH_ID) ?: run { finish(); return }
        val label = intent.getStringExtra(AlarmNotificationHelper.EXTRA_LABEL) ?: ibadahId


        setContent {
            RumahTaqwaTheme {
                AlarmRingScreen(
                    ibadahId = ibadahId,
                    label = label,
                    onDismiss = {
                        AlarmNotificationHelper.dismiss(this, ibadahId)
                        finish()
                    },
                    onSnooze = {
                        sendBroadcast(android.content.Intent(this, SnoozeReceiver::class.java).apply {
                            putExtra(AlarmScheduler.EXTRA_IBADAH_ID, ibadahId)
                        })
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
private fun AlarmRingScreen(ibadahId: String, label: String, onDismiss: () -> Unit, onSnooze: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Waktunya $label", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(32.dp))
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Selesai") }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onSnooze, modifier = Modifier.fillMaxWidth()) { Text("Tunda") }
        }
    }
}