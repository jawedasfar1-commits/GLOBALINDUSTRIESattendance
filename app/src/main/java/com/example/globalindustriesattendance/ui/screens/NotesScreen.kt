package com.example.globalindustriesattendance.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.globalindustriesattendance.data.model.AttendanceUiState

@Composable
fun NotesScreen(
    uiState: AttendanceUiState,
    onSaveNotes: (String) -> Unit,
    onRequestUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var notesText by remember(uiState.notesText) { mutableStateOf(uiState.notesText) }

    fun triggerSave(text: String) {
        if (!uiState.isEditUnlocked) {
            onRequestUnlock()
            Toast.makeText(context, "Please unlock Edit Mode first", Toast.LENGTH_SHORT).show()
        } else {
            onSaveNotes(text)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = "Notes & Observations - Day ${uiState.selectedDay}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Daily Notes, Weather Conditions & Issues",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notesText,
                    onValueChange = {
                        notesText = it
                        triggerSave(it)
                    },
                    placeholder = { Text("Type general notes, weather conditions, or issues for this day here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    enabled = uiState.isEditUnlocked
                )
            }
        }
    }
}
