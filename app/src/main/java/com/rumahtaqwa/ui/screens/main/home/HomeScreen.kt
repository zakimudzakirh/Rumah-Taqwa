package com.rumahtaqwa.ui.screens.main.home

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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumahtaqwa.R
import com.rumahtaqwa.core.util.ThemeMode
import com.rumahtaqwa.core.util.toDayLabel
import com.rumahtaqwa.core.util.toDayNumber
import com.rumahtaqwa.core.util.toDayNumber2Digits
import com.rumahtaqwa.core.util.toFormatDataString
import com.rumahtaqwa.core.util.toFormattedString
import com.rumahtaqwa.data.model.Ibadah
import com.rumahtaqwa.data.model.IbadahSetting
import com.rumahtaqwa.ui.components.CircularProgressChart
import com.rumahtaqwa.ui.components.UpdateIbadahDialog
import com.rumahtaqwa.ui.theme.RumahTaqwaShapes
import java.util.Calendar
import java.util.Date

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    var updateDataDialog by remember { mutableStateOf<Pair<String, Ibadah>?>(null) }

    LaunchedEffect(state.ibadah, state.settings) {
        if (state.ibadah.isNotEmpty() && state.settings?.isNotEmpty() == true) {
            viewModel.fetchWeekly()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "Assalamu'alaikum,",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = state.user?.displayName ?: "-",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = Date().toFormattedString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

//            Box(
//                modifier = Modifier.clickable{
//                    viewModel.setTheme(
//                        if (themeMode == ThemeMode.DARK) {
//                            ThemeMode.LIGHT
//                        } else {
//                            ThemeMode.DARK
//                        }
//                    )
//                }
//            ){
//                Icon(
//                    modifier = Modifier.align(Alignment.Center),
//                    painter = painterResource(R.drawable.ic_bell),
//                    contentDescription = "null",
//                    tint = MaterialTheme.colorScheme.primary
//                )
//            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
            text = "MINGGU INI",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 14.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            viewModel.getCurrentWeekDays().forEach { date ->
                CardDay(date)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .clip(RumahTaqwaShapes.small)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 15.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressChart(
                modifier = Modifier.size(80.dp),
                progress = state.weeklyProgress,
            )

            Spacer(modifier = Modifier.width(25.dp))

            Column {
                Text(
                    text =  "Progress Minggu ini",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text =  "${state.weeklyCount} dari ${state.weeklyTotal} ibadah dilakukan",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp
                )
            }

        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            text = "IBADAH HARI INI",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 14.sp
        )

        LazyColumn {
            items(state.ibadah) {
                if (state.settings?.get(it.id)?.recap == true
                    && (state.settings?.get(it.id)?.perWeek ?: 0) > 0) {
                    ListItemIbadah(
                        ibadah = it,
                        setting = state.settings?.get(it.id),
                        count = state.weekly?.get(it.id) ?: 0,
                        todayCheck = state.today[it.id] ?: "",
                        onClick = {
                            updateDataDialog = Pair(state.today[it.id] ?: "", it)
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }

    updateDataDialog?.let { (value, ibadah) ->
        UpdateIbadahDialog(
            ibadah = ibadah,
            currentValue = value,
            surats = state.surats,
            onDismiss = { updateDataDialog = null },
            onConfirm = { newValue ->
                viewModel.updateData(ibadah.id, newValue)
                updateDataDialog = null
            }
        )
    }
}

@Composable
private fun CardDay(
    date: Date,
) {
    val isToday = date.toDayNumber() == Calendar.getInstance().time.toDayNumber()
    val isPast = date.before(Calendar.getInstance().time)
    Column(
        modifier = Modifier
            .clip(RumahTaqwaShapes.small)
            .background(
                if (isToday){
                    MaterialTheme.colorScheme.primary
                } else if(isPast) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.background
                }
            )
            .padding(vertical = 8.dp, horizontal = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.toDayLabel(),
            color = if (isToday) {
                MaterialTheme.colorScheme.onPrimary          // teks di atas kotak primary
            } else if (isPast) {
                MaterialTheme.colorScheme.onPrimaryContainer // teks di atas kotak primaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            style = MaterialTheme.typography.titleSmall
        )

        Text(
            text = date.toDayNumber2Digits(),
            color = if (isToday) {
                MaterialTheme.colorScheme.onPrimary          // teks di atas kotak primary
            } else if (isPast) {
                MaterialTheme.colorScheme.onPrimaryContainer // teks di atas kotak primaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
private fun ListItemIbadah(
    ibadah: Ibadah,
    setting: IbadahSetting?,
    count: Int,
    todayCheck: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 10.dp)
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ){
            Text(
                text = ibadah.label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${count}/${setting?.perWeek ?: 1} per minggu",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            if (todayCheck.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(end = 5.dp),
                    text = "$todayCheck ${ibadah.unitName}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20))
                    .size(30.dp)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20))
                        .size(26.dp)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    if (todayCheck.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20))
                                .size(20.dp)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}