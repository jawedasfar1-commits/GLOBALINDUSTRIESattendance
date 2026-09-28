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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.globalindustriesattendance.data.model.AttendanceUiState
import com.example.globalindustriesattendance.data.model.DprRecord
import com.example.globalindustriesattendance.data.model.MaterialItem

@Composable
fun DprScreen(
    uiState: AttendanceUiState,
    onSaveDpr: (DprRecord) -> Unit,
    onRequestUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var workText by remember(uiState.dprRecord) { mutableStateOf(uiState.dprRecord.workText) }
    val materials = remember(uiState.dprRecord) {
        mutableStateListOf<MaterialItem>().apply {
            if (uiState.dprRecord.materials.isEmpty()) {
                add(MaterialItem("", "", "Bag"))
            } else {
                addAll(uiState.dprRecord.materials)
            }
        }
    }

    fun triggerSave() {
        if (!uiState.isEditUnlocked) {
            onRequestUnlock()
            Toast.makeText(context, "Please unlock Edit Mode first", Toast.LENGTH_SHORT).show()
        } else {
            onSaveDpr(DprRecord(work = workText, materials = materials.toList()))
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Daily Progress Report (DPR) - Day ${uiState.selectedDay}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Work & Labour Details Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Work & Labour Execution Details",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = workText,
                        onValueChange = {
                            workText = it
                            triggerSave()
                        },
                        placeholder = { Text("E.g. Sushil - AC block masonry\nSom - Column shuttering casting") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        enabled = uiState.isEditUnlocked
                    )
                }
            }
        }

        // Material Consumption Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Material Consumption",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = {
                        if (!uiState.isEditUnlocked) {
                            onRequestUnlock()
                            Toast.makeText(context, "Please unlock Edit Mode first", Toast.LENGTH_SHORT).show()
                        } else {
                            materials.add(MaterialItem("", "", "Bag"))
                            triggerSave()
                        }
                    },
                    enabled = uiState.isEditUnlocked
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Material")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Row")
                }
            }
        }

        // Material Consumption Rows
        itemsIndexed(materials) { index, item ->
            MaterialRow(
                item = item,
                isUnlocked = uiState.isEditUnlocked,
                onUpdate = { updatedItem ->
                    materials[index] = updatedItem
                    triggerSave()
                },
                onDelete = {
                    materials.removeAt(index)
                    triggerSave()
                }
            )
        }
    }
}

@Composable
private fun MaterialRow(
    item: MaterialItem,
    isUnlocked: Boolean,
    onUpdate: (MaterialItem) -> Unit,
    onDelete: () -> Unit
) {
    val unitOptions = listOf("Bag", "Kg", "Nos", "Roll", "Tractor")
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = item.item,
                onValueChange = { onUpdate(item.copy(item = it)) },
                placeholder = { Text("Material Name") },
                modifier = Modifier.weight(2f),
                singleLine = true,
                enabled = isUnlocked
            )

            OutlinedTextField(
                value = item.qty,
                onValueChange = { onUpdate(item.copy(qty = it)) },
                placeholder = { Text("Qty") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                enabled = isUnlocked
            )

            Box(modifier = Modifier.weight(1.2f)) {
                OutlinedTextField(
                    value = item.unit,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Unit",
                            modifier = Modifier.clickable { if (isUnlocked) expanded = true }
                        )
                    },
                    singleLine = true,
                    enabled = isUnlocked
                )
                DropdownMenu(
                    expanded = expanded && isUnlocked,
                    onDismissRequest = { expanded = false }
                ) {
                    unitOptions.forEach { u ->
                        DropdownMenuItem(
                            text = { Text(u) },
                            onClick = {
                                onUpdate(item.copy(unit = u))
                                expanded = false
                            }
                        )
                    }
                }
            }

            IconButton(onClick = onDelete, enabled = isUnlocked) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Row",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
