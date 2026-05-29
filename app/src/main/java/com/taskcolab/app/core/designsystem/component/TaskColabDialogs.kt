package com.taskcolab.app.core.designsystem.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.taskcolab.app.core.designsystem.theme.TaskColabBlue
import com.taskcolab.app.core.designsystem.theme.TaskColabDanger
import com.taskcolab.app.core.designsystem.theme.TaskColabInk
import com.taskcolab.app.core.designsystem.theme.TaskColabMuted
import com.taskcolab.app.core.designsystem.theme.TaskColabWhite

@Composable
fun TaskColabConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Eliminar",
    dismissText: String = "Cancelar",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    destructive: Boolean = true
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TaskColabWhite,
        title = {
            Text(
                text = title,
                color = TaskColabInk,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = message,
                color = TaskColabMuted
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    color = if (destructive) TaskColabDanger else TaskColabBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissText,
                    color = TaskColabBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
