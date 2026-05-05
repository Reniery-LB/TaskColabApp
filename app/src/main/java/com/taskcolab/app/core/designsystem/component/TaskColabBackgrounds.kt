package com.taskcolab.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.taskcolab.app.core.designsystem.theme.TaskColabBlue
import com.taskcolab.app.core.designsystem.theme.TaskColabDesaturatedBlue
import com.taskcolab.app.core.designsystem.theme.TaskColabWhite

@Composable
fun TaskColabGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.00f to TaskColabBlue,
                        0.42f to TaskColabDesaturatedBlue,
                        0.70f to TaskColabWhite,
                        1.00f to TaskColabWhite
                    )
                )
            )
    ) {
        content()
    }
}
