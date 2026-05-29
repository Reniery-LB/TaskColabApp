package com.taskcolab.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import com.taskcolab.app.BuildConfig
import com.taskcolab.app.core.designsystem.theme.TaskColabInk
import com.taskcolab.app.core.designsystem.theme.TaskColabWhite

@Composable
fun UserAvatar(
    fullName: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp,
    backgroundColor: Color = TaskColabWhite,
    textColor: Color = TaskColabInk
) {
    Box(
        modifier = modifier
            .size(size)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        val model = avatarUrl.toAbsoluteAvatarUrl()
        if (model != null) {
            AsyncImage(
                model = model,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
            )
        } else {
            Text(
                text = fullName.toInitials(),
                style = MaterialTheme.typography.headlineMedium,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun String.toInitials(default: String = "RC"): String =
    trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString(separator = "") { it.first().uppercase() }
        .ifBlank { default }

fun String?.toAbsoluteAvatarUrl(): String? {
    val value = this?.trim().orEmpty()
    if (value.isBlank()) return null
    if (value.startsWith("http://") || value.startsWith("https://") || value.startsWith("content://")) {
        return value
    }

    val base = BuildConfig.TASKCOLAB_API_BASE_URL.trimEnd('/')
    val origin = base
        .substringBefore("/assets/api")
        .substringBefore("/PROYECTO_GESTOR_TAREAS/")
        .trimEnd('/')
    return if (value.startsWith("/")) origin + value else "$origin/$value"
}
