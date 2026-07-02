package com.rumahtaqwa.ui.screens.main.rekap

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumahtaqwa.R
import com.rumahtaqwa.core.util.toDayNumber
import com.rumahtaqwa.core.util.toFormatDataString
import com.rumahtaqwa.core.util.toMonthLabel
import com.rumahtaqwa.data.model.Ibadah
import com.rumahtaqwa.ui.components.Header
import com.rumahtaqwa.ui.components.MonthPickerDialog
import com.rumahtaqwa.ui.components.UpdateIbadahDialog
import java.util.Calendar
import java.util.Date

@Composable
fun RekapScreen(
    viewModel: RekapViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val horizontalScroll = rememberScrollState()
    var editMode by remember { mutableStateOf(false) }
    var showPicker by remember { mutableStateOf(false) }
    val calendar = remember(state.date) {
        Calendar.getInstance().apply { time = state.date }
    }
    val selectedMonth = calendar.get(Calendar.MONTH) + 1
    val selectedYear = calendar.get(Calendar.YEAR)

    val context = LocalContext.current

    var editingCell by remember { mutableStateOf<Pair<Date, Ibadah>?>(null) }

    LaunchedEffect(state.ibadah) {
        if (state.ibadah.isNotEmpty() && state.settings?.isNotEmpty() == true) {
            viewModel.fetchLogs()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Header(
            title = "Rekap Data"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!editMode) {
                Button(
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 20.dp),
                    onClick = { showPicker = true }
                ) {
                    Text(
                        text = state.date.toMonthLabel(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else {
                Box{}
            }

            Row {
                if (!editMode) {
                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        onClick = {
                            val file = viewModel.exportPdf(context) ?: return@IconButton

                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, "application/pdf")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            //                        context.startActivity(Intent.createChooser(intent, "Share PDF"))
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_download),
                            contentDescription = null,
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        onClick = { editMode = true }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_pencil),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    Button(
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 20.dp),
                        onClick = {
                            viewModel.onSaveData()
                            editMode = false
                        }
                    ) {
                        Text(
                            text = "Simpan",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontalScroll)
        ) {
            LazyColumn(
                modifier = Modifier.padding(bottom = 60.dp)
            ) {
                stickyHeader {
                    Row {
                        val dividerColor = MaterialTheme.colorScheme.onSurfaceVariant
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .width(40.dp)
                                .padding(vertical = 10.dp)
                                .drawBehind {
                                    drawLine(
                                        color = dividerColor,
                                        start = Offset(size.width, 0f - 20),
                                        end = Offset(size.width, size.height + 20),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                    drawLine(
                                        color = dividerColor,
                                        start = Offset(0f, size.height + 20),
                                        end = Offset(size.width, size.height + 20),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        state.ibadah.forEach { ibadah ->
                            if (state.settings?.get(ibadah.id)?.recap == true) {
                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surface)
                                        .width((ibadah.length + 16).dp)
                                        .padding(vertical = 10.dp)
                                        .drawBehind {
                                            drawLine(
                                                color = dividerColor,
                                                start = Offset(size.width, 0f - 20),
                                                end = Offset(size.width, size.height + 20),
                                                strokeWidth = 1.dp.toPx()
                                            )
                                            drawLine(
                                                color = dividerColor,
                                                start = Offset(0f, size.height + 20),
                                                end = Offset(size.width, size.height + 20),
                                                strokeWidth = 1.dp.toPx()
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        text = ibadah.label,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                            }
                        }
                    }
                }
                items(state.dates) { date ->
                    Row {
                        val dividerColor = MaterialTheme.colorScheme.onSurfaceVariant
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(x = horizontalScroll.value, y = 0) }
                                .zIndex(1f)
                                .background(MaterialTheme.colorScheme.surface)
                                .width(40.dp)
                                .padding(vertical = 10.dp)
                                .drawBehind {
                                    drawLine(
                                        color = dividerColor,
                                        start = Offset(size.width, 0f - 20),
                                        end = Offset(size.width, size.height + 20),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                    drawLine(
                                        color = dividerColor,
                                        start = Offset(0f, size.height + 20),
                                        end = Offset(size.width, size.height + 20),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date.toDayNumber(),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        state.ibadah.forEach { ibadah ->
                            if (state.settings?.get(ibadah.id)?.recap == true) {
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            if (editMode) {
                                                editingCell = Pair(date, ibadah)
                                            }
                                        }
                                        .background(MaterialTheme.colorScheme.background)
                                        .width((ibadah.length + 16).dp)
                                        .padding(vertical = 10.dp)
                                        .drawBehind {
                                            drawLine(
                                                color = dividerColor,
                                                start = Offset(size.width, 0f - 20),
                                                end = Offset(size.width, size.height + 20),
                                                strokeWidth = 1.dp.toPx()
                                            )
                                            drawLine(
                                                color = dividerColor,
                                                start = Offset(0f, size.height + 20),
                                                end = Offset(size.width, size.height + 20),
                                                strokeWidth = 1.dp.toPx()
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        text = if (state.data[date.toFormatDataString()]?.get(ibadah.id)
                                                ?.isNotEmpty() == true
                                        ) {
                                            "${state.data[date.toFormatDataString()]?.get(ibadah.id)}"
                                        } else "-",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    if (showPicker) {
        MonthPickerDialog(
            currentMonth = selectedMonth,
            currentYear = selectedYear,
            onDismiss = { showPicker = false },
            onConfirm = { month, year ->
                viewModel.onMonthSelected(month, year)
                showPicker = false
            }
        )
    }

    editingCell?.let { (date, ibadah) ->
        UpdateIbadahDialog(
            ibadah = ibadah,
            currentValue = state.data[date.toFormatDataString()]?.get(ibadah.id) ?: "",
            surats = state.surats,
            onDismiss = { editingCell = null },
            onConfirm = { newValue ->
                viewModel.updateData(date.toFormatDataString(), ibadah.id, newValue)
                editingCell = null
            }
        )
    }

}

