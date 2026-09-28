package com.example.globalindustriesattendance.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.globalindustriesattendance.data.model.AttendanceUiState
import java.util.Locale

private data class MonthlyTotals(
    val grandDuty: Double,
    val grandOt: Double,
    val grandAmount: Double,
    val grandAdvance: Double,
    val grandBalance: Double,
    val dayDutyTotals: DoubleArray,
    val dayOtTotals: DoubleArray
)

@Composable
fun MonthlyViewScreen(
    uiState: AttendanceUiState,
    onMonthSelected: (String) -> Unit,
    onAdvanceUpdated: (workerId: String, amount: Double) -> Unit,
    onRequestUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val horizontalScrollState = rememberScrollState()
    var expandedMonthMenu by remember { mutableStateOf(false) }

    val monthOptions = remember {
        val list = mutableListOf<String>()
        val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        for (y in 2025..2030) {
            for (m in 1..12) {
                list.add("${months[m - 1]} $y")
            }
        }
        list
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7FB))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- 1. Month Dropdown Toolbar Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Month Selector Dropdown Box
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.selectedYearMonth.ifEmpty { "September 2026" },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF102A43)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Month",
                            tint = Color(0xFF64748B)
                        )
                    }

                    DropdownMenu(
                        expanded = expandedMonthMenu,
                        onDismissRequest = { expandedMonthMenu = false }
                    ) {
                        monthOptions.forEach { mOpt ->
                            DropdownMenuItem(
                                text = { Text(mOpt) },
                                onClick = {
                                    onMonthSelected(mOpt)
                                    expandedMonthMenu = false
                                }
                            )
                        }
                    }
                }

                // Action Toolbar Buttons: Export Sheet & Print
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Exporting Attendance Sheet...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(46.dp)
                    ) {
                        Icon(imageVector = Icons.Default.IosShare, contentDescription = "Export")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Sheet", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "Preparing Print Document...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D68C4)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(46.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = "Print")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- 2. Expand Table Header Button ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    Toast.makeText(context, "Sheet Expanded", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D68C4)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(imageVector = Icons.Default.Fullscreen, contentDescription = "Expand", modifier = Modifier.padding(end = 4.dp))
                Text("Expand Sheet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // --- 3. Monthly Table Matrix ---
        if (uiState.workers.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No records. Add workers first.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            val totals = remember(uiState.workers, uiState.attendanceMap, uiState.advancesMap) {
                var gDuty = 0.0
                var gOt = 0.0
                var gAmt = 0.0
                var gAdv = 0.0
                var gBal = 0.0

                val dDutyTotals = DoubleArray(32)
                val dOtTotals = DoubleArray(32)

                uiState.workers.forEach { worker ->
                    val workerAttendance = uiState.attendanceMap[worker.id] ?: emptyMap()
                    var wDuty = 0.0
                    var wOt = 0.0
                    var wDailyAdv = 0.0
                    for (day in 1..31) {
                        val rec = workerAttendance[day]
                        if (rec != null) {
                            wDuty += rec.duty
                            wOt += rec.ot
                            wDailyAdv += rec.dailyAdvance
                            dDutyTotals[day] += rec.duty
                            dOtTotals[day] += rec.ot
                        }
                    }
                    val amt = (wDuty * worker.dailyRate) + (wOt * (worker.dailyRate / 8.0))
                    val baseAdv = uiState.advancesMap[worker.id] ?: worker.baseAdvance
                    val totAdv = baseAdv + wDailyAdv
                    val bal = amt - totAdv

                    gDuty += wDuty
                    gOt += wOt
                    gAmt += amt
                    gAdv += totAdv
                    gBal += bal
                }

                MonthlyTotals(gDuty, gOt, gAmt, gAdv, gBal, dDutyTotals, dOtTotals)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    // Header Row 1 & Row 2
                    item {
                        Column {
                            // Row 1: Header Titles
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF102A43))
                                    .horizontalScroll(horizontalScrollState)
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell(text = "NAME", width = 130.dp, isHeader = true)
                                TableCell(text = "TRADE", width = 110.dp, isHeader = true)

                                (1..31).forEach { day ->
                                    TableCell(text = "$day", width = 50.dp, isHeader = true)
                                }

                                TableCell(text = "DUTY", width = 70.dp, isHeader = true)
                                TableCell(text = "OT", width = 70.dp, isHeader = true)
                                TableCell(text = "RATE", width = 80.dp, isHeader = true)
                                TableCell(text = "AMOUNT", width = 95.dp, isHeader = true)
                                TableCell(text = "ADVANCE", width = 95.dp, isHeader = true)
                                TableCell(text = "BALANCE", width = 100.dp, isHeader = true)
                            }

                            // Row 2: Sub-headers (D / OT)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1F4E78))
                                    .horizontalScroll(horizontalScrollState)
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell(text = "", width = 130.dp, isHeader = true)
                                TableCell(text = "", width = 110.dp, isHeader = true)

                                (1..31).forEach { _ ->
                                    TableCell(text = "D", width = 25.dp, isHeader = true, fontSize = 10.sp)
                                    TableCell(text = "OT", width = 25.dp, isHeader = true, fontSize = 10.sp)
                                }

                                TableCell(text = "", width = 70.dp, isHeader = true)
                                TableCell(text = "", width = 70.dp, isHeader = true)
                                TableCell(text = "", width = 80.dp, isHeader = true)
                                TableCell(text = "", width = 95.dp, isHeader = true)
                                TableCell(text = "", width = 95.dp, isHeader = true)
                                TableCell(text = "", width = 100.dp, isHeader = true)
                            }
                        }
                    }

                    // Worker Rows
                    items(uiState.workers, key = { it.id }) { worker ->
                        val workerAttendance = uiState.attendanceMap[worker.id] ?: emptyMap()

                        var totalDuty = 0.0
                        var totalOt = 0.0
                        var totalDailyAdv = 0.0
                        for (day in 1..31) {
                            val record = workerAttendance[day]
                            if (record != null) {
                                totalDuty += record.duty
                                totalOt += record.ot
                                totalDailyAdv += record.dailyAdvance
                            }
                        }

                        val rate = worker.dailyRate
                        val amount = (totalDuty * rate) + (totalOt * (rate / 8.0))
                        val baseAdv = uiState.advancesMap[worker.id] ?: worker.baseAdvance
                        val totalAdv = baseAdv + totalDailyAdv
                        val balance = amount - totalAdv

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(horizontalScrollState)
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableCell(text = worker.name, width = 130.dp, fontWeight = FontWeight.Bold, textColor = Color(0xFF102A43))
                            TableCell(text = worker.designation, width = 110.dp, textColor = Color(0xFF64748B))

                            (1..31).forEach { day ->
                                val record = workerAttendance[day]
                                val dutyStr = when (record?.duty) {
                                    1.0 -> "P"
                                    0.5 -> "½"
                                    0.0 -> if ((record.ot) > 0) "A" else "-"
                                    else -> "-"
                                }
                                val otStr = if (record != null && record.ot > 0) "${record.ot}h" else "-"

                                val textColor = when (dutyStr) {
                                    "P" -> Color(0xFF166534)
                                    "½" -> Color(0xFF854D0E)
                                    else -> Color(0xFF64748B)
                                }

                                TableCell(text = dutyStr, width = 25.dp, textColor = textColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                TableCell(text = otStr, width = 25.dp, textColor = Color(0xFF2D68C4), fontSize = 10.sp)
                            }

                            TableCell(text = String.format(Locale.US, "%.1f", totalDuty), width = 70.dp, fontWeight = FontWeight.Bold)
                            TableCell(text = String.format(Locale.US, "%.1f", totalOt), width = 70.dp)
                            TableCell(text = "₹${rate.toInt()}", width = 80.dp)
                            TableCell(text = String.format(Locale.US, "₹%.0f", amount), width = 95.dp, fontWeight = FontWeight.Bold)

                            AdvanceCell(
                                width = 95.dp,
                                currentAdvance = totalAdv,
                                isUnlocked = uiState.isEditUnlocked,
                                onSaveAdvance = { newAmount ->
                                    if (!uiState.isEditUnlocked) {
                                        onRequestUnlock()
                                        Toast.makeText(context, "Please unlock Edit Mode first", Toast.LENGTH_SHORT).show()
                                    } else {
                                        onAdvanceUpdated(worker.id, newAmount)
                                    }
                                }
                            )

                            TableCell(
                                text = String.format(Locale.US, "₹%.0f", balance),
                                width = 100.dp,
                                fontWeight = FontWeight.Bold,
                                textColor = if (balance >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                        }
                        HorizontalDivider(color = Color(0xFFE2E8F0))
                    }

                    // Grand Total Footer Row
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFD0DFEE))
                                .horizontalScroll(horizontalScrollState)
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableCell(text = "GRAND TOTAL", width = 130.dp, fontWeight = FontWeight.Bold, textColor = Color(0xFF102A43))
                            TableCell(text = "-", width = 110.dp)

                            (1..31).forEach { day ->
                                val dStr = if (totals.dayDutyTotals[day] > 0) String.format(Locale.US, "%.1f", totals.dayDutyTotals[day]) else "-"
                                TableCell(text = dStr, width = 50.dp, fontSize = 10.sp, fontWeight = FontWeight.Bold, textColor = Color(0xFF102A43))
                            }

                            TableCell(text = String.format(Locale.US, "%.1f", totals.grandDuty), width = 70.dp, fontWeight = FontWeight.Bold, textColor = Color(0xFF102A43))
                            TableCell(text = String.format(Locale.US, "%.1f", totals.grandOt), width = 70.dp, fontWeight = FontWeight.Bold, textColor = Color(0xFF102A43))
                            TableCell(text = "-", width = 80.dp)
                            TableCell(text = String.format(Locale.US, "₹%.0f", totals.grandAmount), width = 95.dp, fontWeight = FontWeight.Bold, textColor = Color(0xFF102A43))
                            TableCell(text = String.format(Locale.US, "₹%.0f", totals.grandAdvance), width = 95.dp, fontWeight = FontWeight.Bold, textColor = Color(0xFF102A43))
                            TableCell(text = String.format(Locale.US, "₹%.0f", totals.grandBalance), width = 100.dp, fontWeight = FontWeight.Bold, textColor = Color(0xFF102A43))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    width: Dp,
    isHeader: Boolean = false,
    fontWeight: FontWeight = FontWeight.Normal,
    textColor: Color = Color.Unspecified,
    fontSize: TextUnit = 12.sp
) {
    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = 2.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isHeader) Color.White else textColor,
            fontWeight = if (isHeader) FontWeight.Bold else fontWeight,
            fontSize = if (isHeader) 11.sp else fontSize,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AdvanceCell(
    width: Dp,
    currentAdvance: Double,
    isUnlocked: Boolean,
    onSaveAdvance: (Double) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var advanceInput by remember(currentAdvance) { mutableStateOf(currentAdvance.toString()) }

    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isEditing && isUnlocked) {
            OutlinedTextField(
                value = advanceInput,
                onValueChange = { advanceInput = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(85.dp)
            )
            TextButton(
                onClick = {
                    val amount = advanceInput.toDoubleOrNull() ?: currentAdvance
                    onSaveAdvance(amount)
                    isEditing = false
                }
            ) {
                Text("OK", fontSize = 10.sp)
            }
        } else {
            Text(
                text = String.format(Locale.US, "₹%.0f", currentAdvance),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF59E0B),
                modifier = Modifier
                    .padding(2.dp)
                    .background(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
