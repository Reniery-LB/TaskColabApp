package com.taskcolab.app.feature.auth

import androidx.compose.runtime.Composable
import com.taskcolab.app.feature.common.FeaturePlaceholderScreen

@Composable
fun RegisterPlaceholderScreen(onBack: () -> Unit) {
    FeaturePlaceholderScreen(
        title = "Crear cuenta",
        description = "Aqui construiremos el registro con validacion y conexion al backend.",
        actionText = "Regresar",
        onAction = onBack
    )
}
