package com.rumahtaqwa.ui.screens.main.rekap

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumahtaqwa.core.util.toDayNumber
import com.rumahtaqwa.core.util.toFormatDataString
import com.rumahtaqwa.data.model.IbadahSetting
import com.rumahtaqwa.domain.usecase.IbadahUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import androidx.core.graphics.toColorInt
import com.rumahtaqwa.domain.usecase.QuranUseCase

@HiltViewModel
class RekapViewModel @Inject constructor(
    private val ibadahUseCase: IbadahUseCase,
    private val quranUseCase: QuranUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(RekapState())
    val state = _state.asStateFlow()

    init {
        observerIbadah()
        observerIbadahSettings()
        getAllSurat()
    }

    private fun observerIbadah() {
        viewModelScope.launch {
            val listDate = getCurrentMonthDays(Date())
            ibadahUseCase.getIbadah()
                .catch { e -> _state.update { it.copy(error = e.message) } }
                .collect { ibadah ->
                    _state.update {
                        it.copy(
                            ibadah = ibadah,
                            dates = listDate,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun observerIbadahSettings() {
        viewModelScope.launch {
            ibadahUseCase.getIbadahSettings()
                .catch { e ->
                    _state.update { it.copy(error = e.message) }
                }
                .collect { settings ->
                    val updatedSettings = settings?.toMutableMap() ?: mutableMapOf()
                    val listIbadah = _state.value.ibadah
                    listIbadah.forEach { ibadah ->
                        if (updatedSettings[ibadah.id] == null) {
                            updatedSettings[ibadah.id] = IbadahSetting(
                                id = ibadah.id,
                                field = ibadah.field,
                                recap = true,
                                perWeek = 1
                            )
                        }
                    }
                    _state.update {
                        it.copy(
                            settings = updatedSettings,
                            isLoading = false,
                        )
                    }
                }
        }
    }

    fun getCurrentMonthDays(date: Date): List<Date> {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        return (1..maxDay).map {
            calendar.time.also {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }

    fun onMonthSelected(month: Int, year: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val newDate = calendar.time
        val listDate = getCurrentMonthDays(newDate)
        _state.update {
            it.copy(
                date = newDate,
                dates = listDate
            )
        }
        fetchLogs()
    }

    fun fetchLogs() {
        viewModelScope.launch {
            val listDate = _state.value.dates
            val startDate = listDate[0].toFormatDataString()
            val endDate = listDate[listDate.size - 1].toFormatDataString()
            val logs = ibadahUseCase.getLogs(startDate, endDate)
            val logsData: Map<String, Map<String, String>> = logs?.associate { map ->
                    val dateId = map["date"] as? String ?: error("id missing or not a String")
                    val filtered = map
                        .filterKeys { it != "date" }
                        .mapValues { (_, v) -> v as? String ?: "" }
                    dateId to filtered
                }!!
            _state.update {
                it.copy(
                    data = logsData,
                    originalData = logsData
                )
            }
        }
    }

    fun getAllSurat() {
        viewModelScope.launch {
            quranUseCase.getAllSurat()
                .catch { e ->
                    _state.update { it.copy(error = e.message) }
                }
                .collect { surat ->
                    _state.update {
                        it.copy(
                            surats = surat
                        )
                    }
                }
        }
    }

    fun generatePdf(context: Context): File {
        val pdfDocument = PdfDocument()

        val pageWidth = 842f  // A4 landscape
        val margin = 32f
        val dateColWidth = 30f
        val rowHeight = 16f
        val headerY = 60f

        val paintTitle = android.graphics.Paint().apply {
            textSize = 14f
            color = android.graphics.Color.BLACK
            isFakeBoldText = true
        }
        val paintHeader = android.graphics.Paint().apply {
            textSize = 8f
            color = android.graphics.Color.BLACK
            isFakeBoldText = true
        }
        val paintCell = android.graphics.Paint().apply {
            textSize = 8f
            color = android.graphics.Color.BLACK
        }
        val paintLine = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK // sementara
            strokeWidth = 2f // lebih tebal
            style = android.graphics.Paint.Style.STROKE
        }
        val paintHeaderBg = android.graphics.Paint().apply {
            color = "#F0F0F0".toColorInt()
        }
        val paintRowBg = android.graphics.Paint().apply {
            color = "#FAFAFA".toColorInt()
        }

        val density = context.resources.displayMetrics.density

        val filteredIbadah = _state.value.ibadah
            .filter{ ibadah ->
                _state.value.settings?.get(ibadah.id)?.recap == true
            }

        // Hitung lebar tiap kolom berdasarkan label
        val ibadahColWidths = filteredIbadah.map { ibadah ->
                (ibadah.length + 16) * density // dp to px
            }

        // Scale down kalau melebihi usable width
        val usableWidth = pageWidth - (margin * 2) - dateColWidth
        val totalIbadahWidth = ibadahColWidths.sum()
        val scale = if (totalIbadahWidth > usableWidth) usableWidth / totalIbadahWidth else 1f
        val finalColWidths = ibadahColWidths.map { it * scale }

        val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Title
        canvas.drawText("Rekap Ibadah", margin, 30f, paintTitle)

        val tableBottom = headerY + (_state.value.dates.size * rowHeight) + 4f

        // Header row background
        canvas.drawRect(margin, headerY - rowHeight + 4, pageWidth - margin, headerY + 4, paintHeaderBg)

        // Header "#"
        canvas.drawText("#", margin + 4, headerY, paintHeader)

        // Header ibadah
        var headerX = margin + dateColWidth
        filteredIbadah.forEachIndexed { index, ibadah ->
            canvas.drawText(ibadah.label, headerX + 4, headerY, paintHeader)
            headerX += finalColWidths[index]
        }

        canvas.drawLine(margin, headerY + 4, pageWidth - margin, headerY + 4, paintLine)

        // Rows
        _state.value.dates.forEachIndexed { rowIndex, date ->
            val y = headerY + ((rowIndex + 1) * rowHeight)

            // Alternating row background
            if (rowIndex % 2 == 0) {
                canvas.drawRect(margin, y - rowHeight + 4, pageWidth - margin, y + 4, paintRowBg)
            }

            // Tanggal
            canvas.drawText(date.toDayNumber(), margin + 4, y, paintCell)

            // Data ibadah
            var cellX = margin + dateColWidth
            filteredIbadah.forEachIndexed { index, ibadah ->
                val value = _state.value.data[date.toFormatDataString()]?.get(ibadah.id) ?: "-"
                canvas.drawText(value, cellX + 4, y, paintCell)
                cellX += finalColWidths[index]
            }

            // Garis bawah row
            canvas.drawLine(margin, y + 4, pageWidth - margin, y + 4, paintLine)
        }

        // Border tabel
        canvas.drawRect(margin, headerY - rowHeight + 4, pageWidth - margin, tableBottom, paintLine)

        // Garis vertikal kolom tanggal
        canvas.drawLine(
            margin + dateColWidth, headerY - rowHeight + 4,
            margin + dateColWidth, tableBottom,
            paintLine
        )

        // Garis vertikal kolom ibadah
        var lineX = margin + dateColWidth
        finalColWidths.forEach { colWidth ->
            lineX += colWidth
            canvas.drawLine(lineX, headerY - rowHeight + 4, lineX, tableBottom, paintLine)
        }

        pdfDocument.finishPage(page)

        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "rekap_${_state.value.date.toFormatDataString()}.pdf"
        )
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        return file
    }

    fun updateData(dateStr: String, ibadahId: String, value: String) {
        val newData = _state.value.data.toMutableMap()
        val dayData = newData[dateStr]?.toMutableMap() ?: mutableMapOf()
        dayData[ibadahId] = value
        newData[dateStr] = dayData
        _state.update { it.copy(data = newData) }

        // save ke Firestore
        viewModelScope.launch {
            // sesuaikan dengan repository yang kamu punya
        }
    }

    fun onSaveData() {
        viewModelScope.launch {
            try {
                val keyedData = _state.value.dates.associate { date ->
                    date.toFormatDataString() to (_state.value.data[date.toFormatDataString()] ?: emptyMap())
                }
                ibadahUseCase.saveLogsForMonth(
                    data = keyedData,
                    previousData = _state.value.originalData
                )
                _state.update { it.copy(error = null) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
                return@launch
            }
        }
    }

}