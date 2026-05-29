package com.taskcolab.app.feature.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskcolab.app.data.taskcolab.TaskColabRepository
import com.taskcolab.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

data class ProfileUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val user: User? = null,
    val isUploadingAvatar: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: TaskColabRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getCurrentUser()
                .onSuccess { user -> _uiState.update { it.copy(isLoading = false, user = user) } }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "No se pudo cargar el perfil"
                        )
                    }
                }
        }
    }

    fun uploadAvatar(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingAvatar = true, error = null) }
            runCatching { uri.toAvatarPart(context) }
                .mapCatching { repository.uploadAvatar(it).getOrThrow() }
                .onSuccess { user ->
                    _uiState.update { it.copy(isUploadingAvatar = false, user = user) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isUploadingAvatar = false,
                            error = throwable.message ?: "No se pudo subir la foto"
                        )
                    }
                }
        }
    }
}

private fun Uri.toAvatarPart(context: Context): MultipartBody.Part {
    val resolver = context.contentResolver
    val mimeType = resolver.getType(this) ?: "image/jpeg"
    val bytes = resolver.openInputStream(this)?.use { it.readBytes() }
        ?: error("No se pudo abrir la imagen")
    val extension = when (mimeType) {
        "image/png" -> "png"
        "image/gif" -> "gif"
        "image/webp" -> "webp"
        else -> "jpg"
    }
    val fileName = "avatar_${System.currentTimeMillis()}.$extension"
    val body = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("avatar", File(fileName).name, body)
}
