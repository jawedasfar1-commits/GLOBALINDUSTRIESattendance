package com.example.globalindustriesattendance.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.globalindustriesattendance.data.model.AttendanceUiState
import com.example.globalindustriesattendance.data.model.Worker

@Composable
fun DailyViewScreen(
    uiState: AttendanceUiState,
    onDaySelected: (Int) -> Unit,
    onAttendanceUpdated: (workerId: String, day: Int, duty: Double, ot: Double, dailyAdv: Double) -> Unit,
    onBulkMark: (duty: Double) -> Unit,
    onRequestUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val daysList = (1..31).toList()
    val dayListState = rememberLazyListState()

    LaunchedEffect(uiState.selectedDay) {
        if (uiState.selectedDay in 1..31) {
            dayListState.animateScrollToItem(uiState.selectedDay - 1)
        }
    }

    val filteredWorkers = remember(uiState.workers, searchQuery) {
        if (searchQuery.isBlank()) uiState.workers
        else uiState.workers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.designation.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(modifier = modifier.fillMaxSize().background(Color(0xFFF4F7FB))) {
        // Read-only Warning Banner if locked
        if (!uiState.isEditUnlocked) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFEE2E2))
                    .clickable { onRequestUnlock() }
                    .padding(vertical = 10.dp, horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color(0xFF991B1B)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View-Only Mode. Tap to unlock with Admin PIN.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF991B1B),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Day Selector Bar (1..31)
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Date: Day ${uiState.selectedDay}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF102A43)
                )

                // Bulk actions when unlocked
                if (uiState.isEditUnlocked) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { onBulkMark(1.0) },
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("All Present", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = { onBulkMark(0.0) },
                            modifier = Modifier.height(36.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                        ) {
                            Text("Clear Today", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                state = dayListState,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(daysList) { day ->
                    val isSelected = day == uiState.selectedDay
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color(0xFF102A43)
                                else Color.White
                            )
                            .clickable { onDaySelected(day) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$day",
                            color = if (isSelected) Color.White else Color(0xFF102A43),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search worker...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Workers List
        if (filteredWorkers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (uiState.workers.isEmpty()) "No workers added for this site yet. Go to Workers tab to add."
                    else "No workers match search.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredWorkers, key = { it.id }) { worker ->
                    val workerAttendance = uiState.attendanceMap[worker.id]
                    val dayRecord = workerAttendance?.get(uiState.selectedDay)
                    val currentDuty = dayRecord?.duty ?: 0.0
                    val currentOt = dayRecord?.ot ?: 0.0
                    val currentDailyAdv = dayRecord?.dailyAdvance ?: 0.0

                    WorkerAttendanceCard(
                        worker = worker,
                        currentDuty = currentDuty,
                        currentOt = currentOt,
                        currentDailyAdv = currentDailyAdv,
                        isUnlocked = uiState.isEditUnlocked,
                        onDutyChanged = { newDuty ->
                            if (!uiState.isEditUnlocked) {
                                onRequestUnlock()
                                Toast.makeText(context, "Please unlock Edit Mode first", Toast.LENGTH_SHORT).show()
                            } else {
                                onAttendanceUpdated(worker.id, uiState.selectedDay, newDuty, currentOt, currentDailyAdv)
                            }
                        },
                        onOtChanged = { newOt ->
                            if (!uiState.isEditUnlocked) {
                                onRequestUnlock()
                                Toast.makeText(context, "Please unlock Edit Mode first", Toast.LENGTH_SHORT).show()
                            } else {
                                onAttendanceUpdated(worker.id, uiState.selectedDay, currentDuty, newOt, currentDailyAdv)
                            }
                        },
                        onDailyAdvChanged = { newAdv ->
                            if (!uiState.isEditUnlocked) {
                                onRequestUnlock()
                                Toast.makeText(context, "Please unlock Edit Mode first", Toast.LENGTH_SHORT).show()
                            } else {
                                onAttendanceUpdated(worker.id, uiState.selectedDay, currentDuty, currentOt, newAdv)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkerAttendanceCard(
    worker: Worker,
    currentDuty: Double,
    currentOt: Double,
    currentDailyAdv: Double,
    isUnlocked: Boolean,
    onDutyChanged: (Double) -> Unit,
    onOtChanged: (Double) -> Unit,
    onDailyAdvChanged: (Double) -> Unit
) {
    var advInput by remember(currentDailyAdv) {
        mutableStateOf(if (currentDailyAdv > 0) currentDailyAdv.toString() else "")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = worker.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF102A43)
                    )
                    Text(
                        text = worker.designation,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
                Text(
                    text = "₹${worker.dailyRate.toInt()}/d",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D68C4)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Duty Options: P (1), H (0.5), A (0)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DutyBox(
                    label = "P (1)",
                    isSelected = currentDuty == 1.0,
                    selectedBg = Color(0xFFDCFCE7),
                    selectedText = Color(0xFF166534),
                    selectedBorder = Color(0xFF86EFAC),
                    onClick = { onDutyChanged(if (currentDuty == 1.0) 0.0 else 1.0) },
                    modifier = Modifier.weight(1f)
                )

                DutyBox(
                    label = "H (0.5)",
                    isSelected = currentDuty == 0.5,
                    selectedBg = Color(0xFFFEF9C3),
                    selectedText = Color(0xFF854D0E),
                    selectedBorder = Color(0xFFFDE047),
                    onClick = { onDutyChanged(if (currentDuty == 0.5) 0.0 else 0.5) },
                    modifier = Modifier.weight(1f)
                )

                DutyBox(
                    label = "A (0)",
                    isSelected = currentDuty == 0.0,
                    selectedBg = Color(0xFFFEE2E2),
                    selectedText = Color(0xFF991B1B),
                    selectedBorder = Color(0xFFFCA5A5),
                    onClick = { onDutyChanged(0.0) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Inputs Row: OT (Hrs) and Adv (₹)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // OT Control
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFE2E8F0), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "OT (Hrs)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (currentOt > 0) onOtChanged(currentOt - 0.5) },
                            enabled = isUnlocked && currentOt > 0,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease OT")
                        }
                        Text(
                            text = "$currentOt",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF102A43)
                        )
                        IconButton(
                            onClick = { onOtChanged(currentOt + 0.5) },
                            enabled = isUnlocked,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase OT")
                        }
                    }
                }

                // Daily Adv Field
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFE2E8F0), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Adv (₹)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    OutlinedTextField(
                        value = advInput,
                        onValueChange = {
                            advInput = it
                            val valAdv = it.toDoubleOrNull() ?: 0.0
                            onDailyAdvChanged(valAdv)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(65.dp),
                        enabled = isUnlocked,
                        placeholder = { Text("0") }
                    )
                }
            }
        }
    }
}

@Composable
private fun DutyBox(
    label: String,
    isSelected: Boolean,
    selectedBg: Color,
    selectedText: Color,
    selectedBorder: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) selectedBg else Color(0xFFF8FAFC))
            .border(
                width = 1.dp,
                color = if (isSelected) selectedBorder else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) selectedText else Color(0xFF0F172A)
        )
    }
}
