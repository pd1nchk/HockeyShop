package com.podolyanchik.hockeyshop.presentation.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmButton: String,
    dismissButton: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmButton)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissButton)
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    )
} 