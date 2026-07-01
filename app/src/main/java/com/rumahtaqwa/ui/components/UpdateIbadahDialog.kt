package com.rumahtaqwa.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rumahtaqwa.data.model.Ibadah
import com.rumahtaqwa.data.model.quran.Surat

@Composable
fun UpdateIbadahDialog(
    ibadah: Ibadah,
    currentValue: String,
    surats: List<Surat>,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var value by remember { mutableStateOf(currentValue) }

    // range state
    var startSurat by remember { mutableStateOf<Surat?>(null) }
    var startAyah by remember { mutableIntStateOf(1) }
    var endSurat by remember { mutableStateOf<Surat?>(null) }
    var endAyah by remember { mutableIntStateOf(1) }

    // Parse currentValue kalau sudah ada isinya
    // format: "Al-Fatihah: 1 - Al-Baqarah: 280"
    LaunchedEffect(currentValue) {
        if (ibadah.type == "range" && currentValue.isNotEmpty()) {
            val parts = currentValue.split(" - ")
            if (parts.size == 2) {
                val startParts = parts[0].split(": ")
                val endParts = parts[1].split(": ")
                if (endParts.size == 1) {
                    startSurat = surats.find { it.nameId == startParts.getOrElse(0) { "" }.trim() }
                    endSurat = surats.find { it.nameId == startParts.getOrElse(0) { "" }.trim() }
                    startAyah = startParts.getOrElse(1) { "1" }.trim().toIntOrNull() ?: 1
                    endAyah = parts.getOrElse(1) { "1" }.trim().toIntOrNull() ?: 1
                } else {
                    startSurat = surats.find { it.nameId == startParts.getOrElse(0) { "" }.trim() }
                    startAyah = startParts.getOrElse(1) { "1" }.trim().toIntOrNull() ?: 1
                    endSurat = surats.find { it.nameId == endParts.getOrElse(0) { "" }.trim() }
                    endAyah = endParts.getOrElse(1) { "1" }.trim().toIntOrNull() ?: 1
                }
            } else {
                val startParts = currentValue.split(": ")
                startSurat = surats.find { it.nameId == startParts.getOrElse(0) { "" }.trim() }
                endSurat = surats.find { it.nameId == startParts.getOrElse(0) { "" }.trim() }
                startAyah = startParts.getOrElse(1) { "1" }.trim().toIntOrNull() ?: 1
                endAyah = startParts.getOrElse(1) { "1" }.trim().toIntOrNull() ?: 1
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Title
                Text(
                    text = ibadah.label,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Content — when block kamu di sini
                when (ibadah.type) {
                    "text" -> {
                        OutlinedTextField(
                            value = value,
                            onValueChange = { value = it },
                            placeholder = { Text(ibadah.unitName.ifEmpty { "Masukkan nilai" }) },
                            singleLine = true
                        )
                    }
                    "count" -> {
                        OutlinedTextField(
                            value = value,
                            onValueChange = { if (it.all { c -> c.isDigit() }) value = it },
                            placeholder = { Text(ibadah.unitName.ifEmpty { "0" }) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                    "range" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Dari
                            Text(
                                text = "Dari",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Dropdown surat awal
                                SuratDropdown(
                                    modifier = Modifier.weight(1.65f),
                                    surats = surats,
                                    selected = startSurat,
                                    onSelected = {
                                        startSurat = it
                                        startAyah = 1
                                    }
                                )
                                // Dropdown ayat awal
                                AyahDropdown(
                                    modifier = Modifier.weight(1f),
                                    totalAyah = startSurat?.totalAyah ?: 0,
                                    selected = startAyah,
                                    onSelected = { startAyah = it }
                                )
                            }

                            // Sampai
                            Text(
                                text = "Sampai",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                SuratDropdown(
                                    modifier = Modifier.weight(1.65f),
                                    surats = surats,
                                    selected = endSurat,
                                    onSelected = {
                                        endSurat = it
                                        endAyah = 1
                                    }
                                )
                                AyahDropdown(
                                    modifier = Modifier.weight(1f),
                                    totalAyah = endSurat?.totalAyah ?: 0,
                                    selected = endAyah,
                                    onSelected = { endAyah = it }
                                )
                            }

                            // Preview
                            if (startSurat != null && endSurat != null) {
                                val textValue = if (startSurat != endSurat) {
                                    "${startSurat!!.nameId}: $startAyah - ${endSurat!!.nameId}: $endAyah"
                                } else if (startAyah != endAyah) {
                                    "${startSurat!!.nameId}: $startAyah - $endAyah"
                                } else {
                                    "${startSurat!!.nameId}: $startAyah"
                                }
                                Text(
                                    text = textValue,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Batal") }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        val result = when (ibadah.type) {
                            "range" -> {
                                if (startSurat != null && endSurat != null) {
                                    val textValue = if (startSurat != endSurat) {
                                        "${startSurat!!.nameId}: $startAyah - ${endSurat!!.nameId}: $endAyah"
                                    } else if (startAyah != endAyah) {
                                        "${startSurat!!.nameId}: $startAyah - $endAyah"
                                    } else {
                                        "${startSurat!!.nameId}: $startAyah"
                                    }
                                    textValue
                                } else ""
                            }
                            else -> value
                        }
                        if (result.isNotEmpty()) onConfirm(result)
                    }) { Text("Simpan") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuratDropdown(
    modifier: Modifier = Modifier,
    surats: List<Surat>,
    selected: Surat?,
    onSelected: (Surat) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            value = selected?.nameId ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Surat", style = MaterialTheme.typography.bodySmall) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            textStyle = MaterialTheme.typography.bodySmall,
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            surats.forEach { surat ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${surat.id}. ${surat.nameId}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    onClick = {
                        onSelected(surat)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyahDropdown(
    modifier: Modifier = Modifier,
    totalAyah: Int,
    selected: Int,
    onSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { if (totalAyah > 0) expanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            value = if (totalAyah > 0) selected.toString() else "",
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Ayat", style = MaterialTheme.typography.bodySmall) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            textStyle = MaterialTheme.typography.bodySmall,
            enabled = totalAyah > 0,
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            (1..totalAyah).forEach { ayah ->
                DropdownMenuItem(
                    text = { Text(ayah.toString(), style = MaterialTheme.typography.bodySmall) },
                    onClick = {
                        onSelected(ayah)
                        expanded = false
                    }
                )
            }
        }
    }
}