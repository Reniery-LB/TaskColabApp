package com.taskcolab.app.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taskcolab.app.core.designsystem.theme.TaskColabActionBlue
import com.taskcolab.app.core.designsystem.theme.TaskColabBlue
import com.taskcolab.app.core.designsystem.theme.TaskColabWhite

@Composable
fun TaskColabPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(8.dp)
    val gradient = Brush.horizontalGradient(
        colors = if (enabled) {
            listOf(TaskColabBlue, TaskColabActionBlue)
        } else {
            listOf(TaskColabBlue.copy(alpha = 0.45f), TaskColabActionBlue.copy(alpha = 0.45f))
        }
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(
                elevation = 11.dp,
                shape = shape,
                ambientColor = TaskColabBlue.copy(alpha = 0.30f),
                spotColor = TaskColabBlue.copy(alpha = 0.40f)
            )
            .background(gradient, shape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = TaskColabWhite.copy(alpha = if (enabled) 1f else 0.70f),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TaskColabOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(8.dp)

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(
                elevation = 7.dp,
                shape = shape,
                ambientColor = TaskColabBlue.copy(alpha = 0.18f),
                spotColor = TaskColabBlue.copy(alpha = 0.24f)
            ),
        shape = shape,
        border = BorderStroke(2.dp, TaskColabBlue),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = TaskColabWhite,
            contentColor = TaskColabBlue,
            disabledContentColor = TaskColabBlue.copy(alpha = 0.45f)
        ),
        contentPadding = PaddingValues(horizontal = 18.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
