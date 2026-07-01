package com.rumahtaqwa.ui.screens.main.ibadah

import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumahtaqwa.R
import com.rumahtaqwa.data.local.reminder.IbadahReminderEntity
import com.rumahtaqwa.data.model.Ibadah
import com.rumahtaqwa.data.model.IbadahSetting
import com.rumahtaqwa.ui.components.Header
import com.rumahtaqwa.ui.theme.RumahTaqwaShapes

@Composable
fun IbadahScreen(
    viewModel: IbadahViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onScreenResumed()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(bottom =  60.dp),
    ) {
        Header(
            title = "Ibadah"
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(state.ibadah) {
                AccordionCardIbadah(
                    ibadah = it,
                    setting = state.settings?.get(it.id),
                    reminder = state.reminders[it.id],
                    onChangeSettings = { field, value ->
                        viewModel.onChangeSettings(
                            field, it.id, value
                        )
                    },
                    onToggleReminder = { enabled ->
                        viewModel.onToggleReminder(it.id, it.label, enabled)
                    },
                    onChangeReminderTime = { time ->
                        viewModel.onChangeReminderTime(it.id, time)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Button(
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
                disabledContentColor = MaterialTheme.colorScheme.background
            ),
            contentPadding = PaddingValues(vertical = 14.dp),
            onClick = {
                if (!state.isLoading) {
                    viewModel.updateSettings()
                }
            }
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Terapkan",
//                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccordionCardIbadah(
    ibadah: Ibadah,
    setting: IbadahSetting?,
    reminder: IbadahReminderEntity?,
    onChangeSettings: (field: IbadahField, value: String) -> Unit,
    onToggleReminder: (enabled: Boolean) -> Unit,
    onChangeReminderTime: (time: String) -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showFullScreenPermissionDialog by remember { mutableStateOf(false) }

    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "chevron"
    )

    Card(
        modifier = Modifier.padding(horizontal = 10.dp),
        colors = CardColors(
            contentColor = MaterialTheme.colorScheme.surface,
            containerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        // Header (selalu visible, diklik untuk toggle)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 10.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                ibadah.label,
//                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .clip(RumahTaqwaShapes.small)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RumahTaqwaShapes.small
                        )
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        .padding(vertical = 3.dp, horizontal = 8.dp)
                ) {
                    Text(
                        text = "${setting?.perWeek ?: "0"}x/minggu",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Icon(
                    modifier = Modifier.graphicsLayer { rotationZ = rotationAngle },
                    painter = painterResource(R.drawable.ic_chevron_down),
                    tint = MaterialTheme.colorScheme.onSurface,
                    //                if (expanded) Icons.Default.KeyboardArrowUp
                    //                else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }

        // Body (muncul/hilang)
        AnimatedVisibility(visible = expanded) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            Column(modifier = Modifier.padding(horizontal = 15.dp)) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Target pencapaian",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelSmall
                )
                Row(modifier = Modifier.padding(vertical = 10.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RumahTaqwaShapes.small)
                            .size(40.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = RumahTaqwaShapes.small
                            )
                            .clickable {
                                var perWeek = (setting?.perWeek ?: 1) - 1
                                perWeek = if (perWeek < 0) 0 else perWeek
                                onChangeSettings(
                                    IbadahField.PERWEEK,
                                    perWeek.toString()
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.padding(bottom = 5.dp),
                            text = "-",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (setting?.perWeek ?: 0).toString(),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = "x per minggu",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                            lineHeight = 12.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RumahTaqwaShapes.small)
                            .size(40.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = RumahTaqwaShapes.small
                            )
                            .clickable {
                                var perWeek = (setting?.perWeek ?: 0) + 1
                                perWeek = if (perWeek > 7) 7 else perWeek
                                onChangeSettings(
                                    IbadahField.PERWEEK,
                                    perWeek.toString()
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.padding(bottom = 5.dp),
                            text = "+",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Wajib direkap",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelSmall
                    )

                    Switch(
                        checked = setting?.recap ?: false,
                        onCheckedChange = {
                            val isRecap = !(setting?.recap ?: false)
                            onChangeSettings(
                                IbadahField.ISRECAP,
                                isRecap.toString()
                            )
                        },
                        colors = SwitchDefaults.colors(
                            disabledUncheckedTrackColor = MaterialTheme.colorScheme.outline,
                            disabledUncheckedBorderColor = MaterialTheme.colorScheme.outline,
                            disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedThumbColor = MaterialTheme.colorScheme.primary.copy(0.4f)
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pengingat",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelSmall
                    )

                    Switch(
                        checked = reminder?.notify ?: false,
                        onCheckedChange = { enabled ->
                            onToggleReminder(enabled)
                            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                val nm = context.getSystemService(NotificationManager::class.java)
                                if (!nm.canUseFullScreenIntent()) showFullScreenPermissionDialog = true
                            }
                        },
                        colors = SwitchDefaults.colors(
                            disabledUncheckedTrackColor = MaterialTheme.colorScheme.outline,
                            disabledUncheckedBorderColor = MaterialTheme.colorScheme.outline,
                            disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedThumbColor = MaterialTheme.colorScheme.primary.copy(0.4f)
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Jam Pengingat",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Box(
                       modifier = Modifier
                           .border(
                               width = 1.dp,
                               color = if (reminder?.notify == true) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                               shape = RumahTaqwaShapes.small
                           )
                           .clickable(enabled = reminder?.notify == true) { showTimePicker = true }
                           .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = reminder?.notifTime ?: ibadah.defaultTime.ifEmpty { "00:00" },
                            color = if (reminder?.notify == true) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                }
            }
        }
    }

    if (showFullScreenPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showFullScreenPermissionDialog = false },
            title = { Text("Izin Layar Alarm") },
            text = {
                Text(
                    "Untuk menampilkan layar alarm saat HP terkunci, Rumah Taqwa perlu izin " +
                    "\"Tampil di atas aplikasi lain\". Tanpa izin ini, alarm tetap berbunyi " +
                    "dan bergetar, tapi layar alarm tidak akan muncul."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showFullScreenPermissionDialog = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        try {
                            context.startActivity(
                                Intent("android.settings.MANAGE_APP_USE_FULL_SCREEN_INTENTS")
                                    .setData("package:${context.packageName}".toUri())
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        } catch (e: ActivityNotFoundException) {
                            context.startActivity(
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData("package:${context.packageName}".toUri())
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                    }
                }) { Text("Buka Pengaturan") }
            },
            dismissButton = {
                TextButton(onClick = { showFullScreenPermissionDialog = false }) {
                    Text("Nanti Saja")
                }
            }
        )
    }

    if (showTimePicker) {
        val (h, m) = (reminder?.notifTime ?: ibadah.defaultTime.ifEmpty { "00:00" })
            .split(":").map { it.toIntOrNull() ?: 0 }
        val timePickerState = rememberTimePickerState(
            initialHour = h, initialMinute = m, is24Hour = true
        )
        DatePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val time = "%02d:%02d".format(timePickerState.hour, timePickerState.minute)
                    onChangeReminderTime(time)
                    showTimePicker = false
                }) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Batal") }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}