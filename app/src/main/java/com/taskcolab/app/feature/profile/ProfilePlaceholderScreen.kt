package com.taskcolab.app.feature.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.taskcolab.app.R
import com.taskcolab.app.core.designsystem.theme.TaskColabBlue
import com.taskcolab.app.core.designsystem.theme.TaskColabDanger
import com.taskcolab.app.core.designsystem.theme.TaskColabInk
import com.taskcolab.app.core.designsystem.theme.TaskColabWhite
import com.taskcolab.app.feature.auth.validatePassword
import com.taskcolab.app.feature.auth.validatePasswordConfirmation

@Composable
fun ProfilePlaceholderScreen(
    onNavigateToBoards: () -> Unit = {},
    onNavigateToTasks: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToUsers: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var fullName by rememberSaveable { mutableStateOf("Regina Cebreros") }
    var email by rememberSaveable { mutableStateOf("regina@taskcolab.com") }
    var avatarUriValue by rememberSaveable { mutableStateOf<String?>(null) }
    var showNameSheet by remember { mutableStateOf(false) }
    var showPasswordSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val avatarUri = avatarUriValue?.let(Uri::parse)
    val initials = fullName.toInitials()
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            avatarUriValue = uri.toString()
        }
    }

    Scaffold(
        containerColor = TaskColabWhite,
        topBar = {
            ProfileHeader(initials = initials, avatarUri = avatarUri)
        },
        bottomBar = {
            TaskColabBottomBar(
                selectedRoute = ProfileNavItem.PROFILE,
                onBoards = onNavigateToBoards,
                onTasks = onNavigateToTasks,
                onReports = onNavigateToReports,
                onUsers = onNavigateToUsers,
                onProfile = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(start = 18.dp, top = 40.dp, end = 18.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileAvatar(
                initials = initials,
                avatarUri = avatarUri,
                onPickPhoto = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = fullName,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = email,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF9B8C8C),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            ProfileActionRow(
                text = "Cambiar Nombre",
                onClick = { showNameSheet = true }
            )
            Spacer(modifier = Modifier.height(12.dp))
            ProfileActionRow(
                text = "Cambiar Contraseña",
                onClick = { showPasswordSheet = true }
            )
            Spacer(modifier = Modifier.height(14.dp))
            DeleteAccountButton(onClick = { showDeleteDialog = true })
        }
    }

    if (showNameSheet) {
        ChangeNameSheet(
            currentName = fullName,
            onDismiss = { showNameSheet = false },
            onConfirm = {
                fullName = it
                showNameSheet = false
            }
        )
    }

    if (showPasswordSheet) {
        ChangePasswordSheet(
            onDismiss = { showPasswordSheet = false },
            onConfirm = { showPasswordSheet = false }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = TaskColabWhite,
            title = {
                Text(
                    text = "Eliminar mi Cuenta",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Esta accion no se puede deshacer.",
                    color = Color(0xFF666666)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        fullName = "Cuenta Eliminada"
                        email = "sin correo"
                        showDeleteDialog = false
                    }
                ) {
                    Text(text = "Eliminar", color = TaskColabDanger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = "Cancelar", color = TaskColabBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun ProfileHeader(
    initials: String,
    avatarUri: Uri?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TaskColabBlue)
            .padding(start = 18.dp, top = 46.dp, end = 22.dp, bottom = 26.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Perfil",
            style = MaterialTheme.typography.headlineLarge,
            color = TaskColabWhite,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(TaskColabWhite, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (avatarUri != null) {
                AsyncImage(
                    model = avatarUri,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
            } else {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TaskColabInk,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ProfileAvatar(
    initials: String,
    avatarUri: Uri?,
    onPickPhoto: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(132.dp)
            .clickable(onClick = onPickPhoto),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .background(TaskColabBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (avatarUri != null) {
                AsyncImage(
                    model = avatarUri,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                )
            } else {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.displayMedium,
                    color = TaskColabWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 1.dp, y = 4.dp)
                .size(52.dp)
                .background(Color(0xFF1F5BFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.camara_fotografica),
                contentDescription = null,
                colorFilter = ColorFilter.tint(TaskColabWhite),
                modifier = Modifier.size(29.dp)
            )
        }
    }
}

@Composable
private fun ProfileActionRow(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .border(BorderStroke(1.dp, Color(0xFF9B8C8C)), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = TaskColabWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 18.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(id = R.drawable.icn_flecha_derecha),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun DeleteAccountButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD6D6)),
        border = BorderStroke(1.dp, Color(0xFFFF5B5B)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
    ) {
        Text(
            text = "Eliminar mi Cuenta",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Red,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangeNameSheet(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newName by rememberSaveable { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val canSave = newName.isNotBlank() && newName.trim() != currentName

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TaskColabWhite,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        dragHandle = { ProfileSheetHandle() }
    ) {
        ProfileSheetContent(title = "Cambiar Nombre") {
            LabeledProfileField(
                label = "Nuevo nombre completo",
                value = newName,
                onValueChange = { newName = it.take(80) },
                placeholder = "Ingrese su nombre completo",
                singleLine = true
            )
            Spacer(modifier = Modifier.height(40.dp))
            ConfirmQuestion()
            Spacer(modifier = Modifier.height(28.dp))
            SheetActionButtons(
                canSave = canSave,
                onCancel = onDismiss,
                onSave = { onConfirm(newName.trim()) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var currentPasswordError by rememberSaveable { mutableStateOf<String?>(null) }
    var newPasswordError by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmPasswordError by rememberSaveable { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val canSave = currentPassword.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank()

    fun submit() {
        val currentError = validatePassword(currentPassword)
        val newError = validatePassword(newPassword)
        val confirmationError = validatePasswordConfirmation(newPassword, confirmPassword)

        currentPasswordError = currentError
        newPasswordError = newError
        confirmPasswordError = confirmationError

        if (currentError == null && newError == null && confirmationError == null) {
            onConfirm()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TaskColabWhite,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        dragHandle = { ProfileSheetHandle() }
    ) {
        ProfileSheetContent(title = "Cambiar Contraseña") {
            LabeledProfileField(
                label = "Contraseña actual",
                value = currentPassword,
                onValueChange = {
                    currentPassword = it.take(64)
                    currentPasswordError = null
                },
                placeholder = "Contraseña actual",
                singleLine = true,
                password = true,
                error = currentPasswordError
            )
            Spacer(modifier = Modifier.height(16.dp))
            LabeledProfileField(
                label = "Nueva contraseña",
                value = newPassword,
                onValueChange = {
                    newPassword = it.take(64)
                    newPasswordError = null
                    confirmPasswordError = null
                },
                placeholder = "Nueva contraseña (mín. 8 caracteres)",
                singleLine = true,
                password = true,
                error = newPasswordError
            )
            Spacer(modifier = Modifier.height(16.dp))
            LabeledProfileField(
                label = "Confirmar contraseña",
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it.take(64)
                    confirmPasswordError = null
                },
                placeholder = "Confirmar tu nueva contraseña",
                singleLine = true,
                password = true,
                error = confirmPasswordError
            )
            Spacer(modifier = Modifier.height(34.dp))
            ConfirmQuestion()
            Spacer(modifier = Modifier.height(28.dp))
            SheetActionButtons(
                canSave = canSave,
                onCancel = onDismiss,
                onSave = { submit() }
            )
        }
    }
}

@Composable
private fun ProfileSheetHandle() {
    Box(
        modifier = Modifier
            .padding(top = 12.dp, bottom = 18.dp)
            .width(67.dp)
            .height(6.dp)
            .background(Color(0xFFD9D9D9), RoundedCornerShape(50))
    )
}

@Composable
private fun ProfileSheetContent(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 38.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        content()
    }
}

@Composable
private fun LabeledProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = false,
    password: Boolean = false,
    error: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF9C9C9C),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(8.dp), ambientColor = Color.Black.copy(alpha = 0.16f)),
            shape = RoundedCornerShape(8.dp),
            singleLine = singleLine,
            isError = error != null,
            visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = if (password) KeyboardType.Password else KeyboardType.Text),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                errorTextColor = Color.Black,
                focusedBorderColor = Color(0xFF9AB1FF),
                unfocusedBorderColor = Color(0xFF9AB1FF),
                errorBorderColor = TaskColabDanger,
                cursorColor = TaskColabBlue,
                errorCursorColor = TaskColabBlue,
                focusedContainerColor = TaskColabWhite,
                unfocusedContainerColor = TaskColabWhite,
                errorContainerColor = TaskColabWhite
            )
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.labelMedium,
                color = TaskColabDanger,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun ConfirmQuestion() {
    Text(
        text = "¿Confirmas los cambios realizados?",
        style = MaterialTheme.typography.labelLarge,
        color = Color(0xFF666666),
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SheetActionButtons(
    canSave: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(containerColor = TaskColabWhite),
            border = BorderStroke(1.dp, TaskColabBlue),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(36.dp)
        ) {
            Text(
                text = "Cancelar",
                style = MaterialTheme.typography.labelLarge,
                color = TaskColabBlue,
                fontWeight = FontWeight.Bold
            )
        }
        Button(
            onClick = onSave,
            enabled = canSave,
            colors = ButtonDefaults.buttonColors(
                containerColor = TaskColabBlue,
                contentColor = TaskColabWhite,
                disabledContainerColor = Color(0xFFE0E0E0),
                disabledContentColor = Color(0xFF8A8A8A)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(36.dp)
        ) {
            Text(
                text = "Confirmar",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private enum class ProfileNavItem(
    val label: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
) {
    BOARDS("Tableros", R.drawable.icn_tableros_seleccionado, R.drawable.icn_tableros_seleccionado),
    TASKS("Tareas", R.drawable.icn_tareas_seleccionado, R.drawable.icn_tareas),
    REPORTS("Reportes", R.drawable.icn_reportes_seleccionado, R.drawable.icn_reportes),
    USERS("Usuarios", R.drawable.icn_usuarios_seleccionado, R.drawable.icn_usuarios),
    PROFILE("Perfil", R.drawable.icn_perfil_seleccionado, R.drawable.icn_perfil)
}

@Composable
private fun TaskColabBottomBar(
    selectedRoute: ProfileNavItem,
    onBoards: () -> Unit,
    onTasks: () -> Unit,
    onReports: () -> Unit,
    onUsers: () -> Unit,
    onProfile: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TaskColabBlue)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 22.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavButton(ProfileNavItem.BOARDS, selectedRoute == ProfileNavItem.BOARDS, onBoards)
        BottomNavButton(ProfileNavItem.TASKS, selectedRoute == ProfileNavItem.TASKS, onTasks)
        BottomNavButton(ProfileNavItem.REPORTS, selectedRoute == ProfileNavItem.REPORTS, onReports)
        BottomNavButton(ProfileNavItem.USERS, selectedRoute == ProfileNavItem.USERS, onUsers)
        BottomNavButton(ProfileNavItem.PROFILE, selectedRoute == ProfileNavItem.PROFILE, onProfile)
    }
}

@Composable
private fun BottomNavButton(
    item: ProfileNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(58.dp)
            .then(
                if (selected) {
                    Modifier.background(TaskColabWhite, CircleShape)
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = if (selected) item.selectedIcon else item.unselectedIcon),
            contentDescription = item.label,
            colorFilter = if (!selected && item == ProfileNavItem.BOARDS) {
                ColorFilter.tint(TaskColabWhite)
            } else {
                null
            },
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) TaskColabBlue else TaskColabWhite,
            maxLines = 1
        )
    }
}

private fun String.toInitials(): String {
    return trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString(separator = "") { it.first().uppercase() }
        .ifBlank { "RC" }
}
