package com.rumahtaqwa.ui.screens.main.settings

import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumahtaqwa.R
import com.rumahtaqwa.core.util.ThemeMode
import com.rumahtaqwa.ui.components.Header
import com.rumahtaqwa.ui.theme.RumahTaqwaShapes

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val user = viewModel.user
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var canUseFullScreenIntent by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                context.getSystemService(NotificationManager::class.java).canUseFullScreenIntent()
            else true
        )
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                canUseFullScreenIntent = context.getSystemService(NotificationManager::class.java)
                    .canUseFullScreenIntent()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Header(
            title = "Pengaturan"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp)
                .padding(bottom = 60.dp)
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RumahTaqwaShapes.medium
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(vertical = 15.dp, horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(100))
                            .background(
                                MaterialTheme.colorScheme.primary.copy(0.3f)
                            ),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = user?.displayName?.let {
                                "${it[0]}${it[1]}".uppercase()
                            } ?: "",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Column(
                        modifier = Modifier.padding(start = 15.dp)
                    ) {
                        Text(
                            text = user?.displayName ?: "",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = user?.email ?: "",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }

            }

            Text(
                text = "NOTIFIKASI",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )

            ListItem(
                icon = R.drawable.ic_alarm,
                label = "Aktifkan Pengingat",
                trailingIcon = {
                    Switch(
                        checked = canUseFullScreenIntent,
                        onCheckedChange = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                try {
                                    context.startActivity(
                                        Intent("android.settings.MANAGE_APP_USE_FULL_SCREEN_INTENTS")
                                            .setData("package:${context.packageName}".toUri())
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                } catch (e: android.content.ActivityNotFoundException) {
                                    context.startActivity(
                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            .setData("package:${context.packageName}".toUri())
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                }
                            }
                        },
                        enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
                        colors = SwitchDefaults.colors(
                            disabledUncheckedTrackColor = MaterialTheme.colorScheme.outline,
                            disabledUncheckedBorderColor = MaterialTheme.colorScheme.outline,
                            disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedThumbColor = MaterialTheme.colorScheme.primary.copy(0.4f)
                        )
                    )
                }
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.4.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ListItem(
                icon = R.drawable.ic_speaker,
                label = "Suara Notifikasi",
                trailingIcon = {
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { viewModel.setSoundEnabled(it) },
                        colors = SwitchDefaults.colors(
                            disabledUncheckedTrackColor = MaterialTheme.colorScheme.outline,
                            disabledUncheckedBorderColor = MaterialTheme.colorScheme.outline,
                            disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedThumbColor = MaterialTheme.colorScheme.primary.copy(0.4f)
                        )
                    )
                }
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.4.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "TAMPILAN",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )

            ListItem(
                icon = R.drawable.ic_darkmode,
                label = "Mode Gelap",
                trailingIcon = {
                    Switch(
                        checked = themeMode == ThemeMode.DARK,
                        onCheckedChange = {
                            val theme = if (themeMode == ThemeMode.DARK) {
                                ThemeMode.LIGHT
                            } else {
                                ThemeMode.DARK
                            }
                            viewModel.setTheme(theme)
                        },
                        colors = SwitchDefaults.colors(
                            disabledUncheckedTrackColor = MaterialTheme.colorScheme.outline,
                            disabledUncheckedBorderColor = MaterialTheme.colorScheme.outline,
                            disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedThumbColor = MaterialTheme.colorScheme.primary.copy(0.4f)
                        )
                    )
                }
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.4.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "AKUN",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )

            ListItem(
                icon = R.drawable.ic_logout,
                label = "Keluar",
                leadingColor = Color.Red,
                trailingColor = Color.Red,
                labelColor = Color.Red,
                onClick = onLogout
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.4.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

    }

}

@Composable
private fun ListItem(
    icon: Int,
    label: String,
    leadingColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    trailingColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{ if(onClick != null) onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(end = 15.dp),
                painter = painterResource(icon),
                contentDescription = null,
                tint = leadingColor
            )
            Text(
                text = label,
                color = labelColor,
                style = MaterialTheme.typography.labelLarge
            )
        }

        if (trailingIcon == null) {
            Icon(
                modifier = Modifier.padding(vertical = 12.dp).size(24.dp),
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = trailingColor
            )
        } else {
            trailingIcon()
        }

    }
}