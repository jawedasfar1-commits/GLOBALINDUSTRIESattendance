package com.example.globalindustriesattendance.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.globalindustriesattendance.data.model.AttendanceUiState
import com.example.globalindustriesattendance.data.model.Worker

@Composable
fun WorkersScreen(
    uiState: AttendanceUiState,
    onSaveWorker: (Worker) -> Unit,
    onDeleteWorker: (String) -> Unit,
    onRequestUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showWorkerDialog by remember { mutableStateOf(false) }
    var workerToEdit by remember { mutableStateOf<Worker?>(null) }
    var workerToDelete by remember { mutableStateOf<Worker?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (!uiState.isEditUnlocked) {
                        onRequestUnlock()
                        Toast.makeText(context, "Please unlock Edit Mode first", Toast.LENGTH_SHORT).show()
                    } else {
                        workerToEdit = null
                        showWorkerDialog = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Worker")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp)
        ) {
            Text(
                text = "Workers Management (${uiState.workers.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (uiState.workers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No workers registered. Tap + to add worker.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.workers, key = { it.id }) { worker ->
                        val currentAdv = uiState.advancesMap[worker.id] ?: worker.baseAdvance
                        WorkerCard(
                            worker = worker,
                            currentAdv = currentAdv,
                            onEdit = {
                                if (!uiState.isEditUnlocked) {
                                    onRequestUnlock()
                                    Toast.makeText(context, "Please unlock Edit Mode first", Toast.LENGTH_SHORT).show()
                                } else {
                                    workerToEdit = worker
                                    showWorkerDialog = true
                                }
                            },
                            onDelete = {
                                if (!uiState.isEditUnlocked) {
                                    onRequestUnlock()
                                    Toast.makeText(context, "Please unlock Edit Mode first", Toast.LENGTH_SHORT).show()
                                } else {
                                    workerToDelete = worker
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Worker Dialog
    if (showWorkerDialog) {
        WorkerFormDialog(
            worker = workerToEdit,
            onDismiss = { showWorkerDialog = false },
            onSave = { updatedWorker ->
                onSaveWorker(updatedWorker)
                showWorkerDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    workerToDelete?.let { worker ->
        AlertDialog(
            onDismissRequest = { workerToDelete = null },
            title = { Text("Delete Worker") },
            text = { Text("Are you sure you want to delete ${worker.name}? This will remove all attendance records for this worker.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteWorker(worker.id)
                        workerToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { workerToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun WorkerCard(
    worker: Worker,
    currentAdv: Double,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = worker.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${worker.designation} • Rate: ₹${worker.dailyRate.toInt()}/day",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Base Advance: ₹${currentAdv.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Worker")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Worker",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkerFormDialog(
    worker: Worker?,
    onDismiss: () -> Unit,
    onSave: (Worker) -> Unit
) {
    var name by remember { mutableStateOf(worker?.name ?: "") }
    var designation by remember { mutableStateOf(worker?.designation ?: "Mason") }
    var dailyRateText by remember { mutableStateOf(worker?.dailyRate?.toInt()?.toString() ?: "650") }
    var baseAdvText by remember { mutableStateOf(worker?.baseAdvance?.toInt()?.toString() ?: "0") }

    val tradeOptions = listOf(
        "Mason",
        "Fitter",
        "Helper",
        "Welder",
        "Bar Bender",
        "Carpenter",
        "Electrician",
        "Site Supervisor"
    )
    var expandedTrade by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (worker == null) "Add New Worker" else "Edit Worker") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Worker Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = designation,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Designation / Trade") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Trade",
                                modifier = Modifier.clickable { expandedTrade = true }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = expandedTrade,
                        onDismissRequest = { expandedTrade = false }
                    ) {
                        tradeOptions.forEach { trade ->
                            DropdownMenuItem(
                                text = { Text(trade) },
                                onClick = {
                                    designation = trade
                                    expandedTrade = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = dailyRateText,
                    onValueChange = { dailyRateText = it },
                    label = { Text("Daily Wage Rate (₹)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = baseAdvText,
                    onValueChange = { baseAdvText = it },
                    label = { Text("Base / Opening Advance (₹)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val rate = dailyRateText.toDoubleOrNull() ?: 0.0
                    val baseAdv = baseAdvText.toDoubleOrNull() ?: 0.0
                    val updated = (worker ?: Worker()).copy(
                        name = name.trim(),
                        desig = designation.trim(),
                        rate = rate,
                        baseAdvance = baseAdv
                    )
                    onSave(updated)
                },
                enabled = name.isNotBlank() && designation.isNotBlank()
            ) {
                Text("Save Details")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
