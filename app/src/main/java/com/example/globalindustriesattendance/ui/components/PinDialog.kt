package com.example.globalindustriesattendance.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun PinDialog(
    onDismiss: () -> Unit,
    onConfirmPin: (String) -> Boolean
) {
    var pinText by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Enter Admin PIN") },
        text = {
            OutlinedTextField(
                value = pinText,
                onValueChange = {
                    pinText = it
                    isError = false
                },
                label = { Text("PIN") },
                singleLine = true,
                isError = isError,
                supportingText = if (isError) {
                    { Text("Incorrect PIN. Please try again.") }
                } else null,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val success = onConfirmPin(pinText)
                    if (!success) {
                        isError = true
                    }
                }
            ) {
                Text("Unlock")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
