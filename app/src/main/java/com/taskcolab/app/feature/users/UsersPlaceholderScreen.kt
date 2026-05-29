package com.taskcolab.app.feature.users

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.taskcolab.app.R
import com.taskcolab.app.core.designsystem.component.UserAvatar
import com.taskcolab.app.core.designsystem.theme.TaskColabBlue
import com.taskcolab.app.core.designsystem.theme.TaskColabInk
import com.taskcolab.app.core.designsystem.theme.TaskColabWhite
import com.taskcolab.app.feature.auth.validateEmail
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskcolab.app.core.designsystem.component.TaskColabConfirmDialog
import com.taskcolab.app.domain.model.UserRole

@Composable
fun UsersPlaceholderScreen(
    onNavigateToBoards: () -> Unit = {},
    onNavigateToTasks: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToUsers: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onOpenChat: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: UsersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val canManageUsers = uiState.currentUser?.role == UserRole.ADMIN
    val users = uiState.users.map {
        UserListItem(
            id = it.id,
            fullName = it.fullName,
            email = it.email,
            assignedTasks = it.assignedTasks,
            taskCount = it.taskCount,
            notes = it.notes,
            avatarUrl = it.avatarUrl
        )
    }
    var sheetMode by remember { mutableStateOf<UserSheetMode?>(null) }
    var userToDelete by remember { mutableStateOf<UserListItem?>(null) }

    Scaffold(
        containerColor = TaskColabWhite,
        topBar = {
            UsersHeader(
                currentUserName = uiState.currentUser?.fullName.orEmpty(),
                currentUserAvatarUrl = uiState.currentUser?.avatarUrl,
                onOpenChat = onOpenChat,
                onLogout = onLogout
            )
        },
        bottomBar = {
            TaskColabBottomBar(
                selectedRoute = UsersNavItem.USERS,
                onBoards = onNavigateToBoards,
                onTasks = onNavigateToTasks,
                onReports = onNavigateToReports,
                onUsers = onNavigateToUsers,
                onProfile = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 24.dp, top = 22.dp, end = 24.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(users, key = { it.id }) { user ->
                UserCard(
                    user = user,
                    canManageUsers = canManageUsers,
                    onEdit = { sheetMode = UserSheetMode.Edit(user) },
                    onDelete = { userToDelete = user }
                )
            }

            if (uiState.isLoading || uiState.error != null || users.isEmpty()) {
                item {
                    UsersStateMessage(
                        isLoading = uiState.isLoading,
                        error = uiState.error
                    )
                }
            }

            if (canManageUsers) {
                item {
                    AddUserButton(onClick = { sheetMode = UserSheetMode.Create })
                }
            }
        }
    }

    if (canManageUsers) sheetMode?.let { mode ->
        UserFormSheet(
            mode = mode,
            availableTasks = uiState.taskTitles,
            onDismiss = { sheetMode = null },
            onSave = { form ->
                when (mode) {
                    UserSheetMode.Create -> {
                        viewModel.addLocalUser(form)
                    }
                    is UserSheetMode.Edit -> {
                        viewModel.updateLocalUser(mode.user.id, form)
                    }
                }
                sheetMode = null
            }
        )
    }

    if (canManageUsers) userToDelete?.let { user ->
        TaskColabConfirmDialog(
            title = "Eliminar usuario",
            message = "Se eliminará a ${user.fullName} de la lista de usuarios.",
            onConfirm = {
                viewModel.deleteLocalUser(user.id)
                userToDelete = null
            },
            onDismiss = { userToDelete = null }
        )
    }
}

@Composable
private fun UsersStateMessage(
    isLoading: Boolean,
    error: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when {
                isLoading -> "Cargando usuarios..."
                error != null -> "No se pudieron cargar usuarios"
                else -> "No hay usuarios registrados"
            },
            style = MaterialTheme.typography.titleMedium,
            color = TaskColabInk,
            fontWeight = FontWeight.Bold
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF777777)
            )
        }
    }
}

@Composable
private fun UsersHeader(
    currentUserName: String,
    currentUserAvatarUrl: String?,
    onOpenChat: () -> Unit,
    onLogout: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TaskColabBlue)
            .padding(start = 18.dp, top = 46.dp, end = 22.dp, bottom = 26.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Usuarios",
            style = MaterialTheme.typography.headlineLarge,
            color = TaskColabWhite,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onOpenChat) {
            Icon(
                imageVector = Icons.Filled.Chat,
                contentDescription = "Abrir chat",
                tint = TaskColabWhite
            )
        }
        IconButton(onClick = onLogout) {
            Icon(
                imageVector = Icons.Filled.Logout,
                contentDescription = "Cerrar sesión",
                tint = TaskColabWhite
            )
        }
        UserAvatar(
            fullName = currentUserName,
            avatarUrl = currentUserAvatarUrl,
            size = 60.dp
        )
    }
}

@Composable
private fun UserCard(
    user: UserListItem,
    canManageUsers: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(8.dp), ambientColor = Color.Black.copy(alpha = 0.1f))
            .border(BorderStroke(1.dp, Color(0xFFCFCFCF)), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = TaskColabWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(7.dp)
                    .fillMaxHeight()
                    .background(TaskColabBlue)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 18.dp, top = 12.dp, end = 8.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF777777)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UserTag(
                        text = "${user.taskCount} ${if (user.taskCount == 1) "Tarea" else "Tareas"}",
                        selected = true
                    )
                    if (user.notes.isNotBlank()) {
                        UserTag(
                            text = "\"${user.notes}\"",
                            selected = false,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            if (canManageUsers) {
                Column(
                    modifier = Modifier
                        .width(54.dp)
                        .fillMaxSize()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color(0xFFE9F0FF), RoundedCornerShape(4.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icn_editar),
                            contentDescription = "Editar usuario",
                            modifier = Modifier.size(31.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color(0xFFFFDADA), RoundedCornerShape(4.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icn_basura),
                            contentDescription = "Eliminar usuario",
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserTag(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(if (selected) Color(0xFFD9E3FF) else Color(0xFFF1F1F1), RoundedCornerShape(3.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) TaskColabBlue else Color(0xFF777777),
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
private fun AddUserButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .dashedBorder(Color(0xFF9B8C8C), 8.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+ Añadir Usuario",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF948888),
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserFormSheet(
    mode: UserSheetMode,
    availableTasks: List<String>,
    onDismiss: () -> Unit,
    onSave: (UserForm) -> Unit
) {
    val editingUser = (mode as? UserSheetMode.Edit)?.user
    var fullName by remember(mode) { mutableStateOf(editingUser?.fullName ?: "") }
    var email by remember(mode) { mutableStateOf(editingUser?.email ?: "") }
    var notes by remember(mode) { mutableStateOf(editingUser?.notes ?: "") }
    val selectedTasks = remember(mode) {
        mutableStateListOf<String>().apply {
            editingUser?.assignedTasks
                ?.filter { it in availableTasks }
                ?.let(::addAll)
        }
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val emailError = validateEmail(email)
    val visibleEmailError = emailError.takeIf { email.isNotBlank() }
    val canSave = fullName.isNotBlank() && emailError == null

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TaskColabWhite,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 18.dp, bottom = 8.dp)
                    .width(67.dp)
                    .height(10.dp)
                    .background(Color(0xFFD9D9D9), RoundedCornerShape(50))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (mode is UserSheetMode.Edit) "Editar Usuario" else "Nuevo Usuario",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserTextField(
                    value = fullName,
                    onValueChange = { fullName = it.take(80) },
                    placeholder = "Nombre completo",
                    singleLine = true
                )
                UserTextField(
                    value = email,
                    onValueChange = { email = it.take(80) },
                    placeholder = "Correo electrónico",
                    singleLine = true,
                    error = visibleEmailError
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "ASIGNAR TAREAS",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF6D6D6D),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(8.dp), ambientColor = Color.Black.copy(alpha = 0.16f))
                        .border(BorderStroke(1.dp, Color(0xFF9AB1FF)), RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8F8F8), RoundedCornerShape(8.dp))
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    availableTasks.forEach { task ->
                        TaskChoiceRow(
                            task = task,
                            selected = task in selectedTasks,
                            onClick = {
                                if (task in selectedTasks) {
                                    selectedTasks.remove(task)
                                } else {
                                    selectedTasks.add(task)
                                }
                            }
                        )
                    }
                    if (availableTasks.isEmpty()) {
                        Text(
                            text = "No hay tareas disponibles",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF777777),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            UserTextField(
                value = notes,
                onValueChange = { notes = it.take(160) },
                placeholder = "Notas (Ej: Excelente en PHP)",
                minLines = 3
            )

            SheetActionButtons(
                cancelText = "Cancelar",
                saveText = if (mode is UserSheetMode.Edit) "Guardar" else "Crear",
                canSave = canSave,
                onCancel = onDismiss,
                onSave = {
                    onSave(
                        UserForm(
                            fullName = fullName.trim(),
                            email = email.trim(),
                            tasks = selectedTasks.toList(),
                            notes = notes.trim()
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun SheetActionButtons(
    cancelText: String,
    saveText: String,
    canSave: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(containerColor = TaskColabWhite),
            border = BorderStroke(1.dp, TaskColabBlue),
            shape = RoundedCornerShape(7.dp),
            modifier = Modifier
                .weight(1f)
                .height(46.dp)
        ) {
            Text(
                text = cancelText,
                style = MaterialTheme.typography.titleMedium,
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
            shape = RoundedCornerShape(7.dp),
            modifier = Modifier
                .weight(1f)
                .height(46.dp)
        ) {
            Text(
                text = saveText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun UserTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = false,
    minLines: Int = 1,
    error: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF8E8585),
                    fontWeight = FontWeight.Bold
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(8.dp), ambientColor = Color.Black.copy(alpha = 0.16f)),
            shape = RoundedCornerShape(8.dp),
            singleLine = singleLine,
            minLines = minLines,
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                errorTextColor = Color.Black,
                focusedBorderColor = Color(0xFF9AB1FF),
                unfocusedBorderColor = Color(0xFF9AB1FF),
                errorBorderColor = Color(0xFFD71920),
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
                color = Color(0xFFD71920),
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun TaskChoiceRow(
    task: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(26.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = selected,
            onCheckedChange = { onClick() },
            modifier = Modifier.size(28.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = TaskColabBlue,
                uncheckedColor = Color(0xFF8E8585),
                checkmarkColor = TaskColabWhite
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = task,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

private enum class UsersNavItem(
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
    selectedRoute: UsersNavItem,
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
        BottomNavButton(UsersNavItem.BOARDS, selectedRoute == UsersNavItem.BOARDS, onBoards)
        BottomNavButton(UsersNavItem.TASKS, selectedRoute == UsersNavItem.TASKS, onTasks)
        BottomNavButton(UsersNavItem.REPORTS, selectedRoute == UsersNavItem.REPORTS, onReports)
        BottomNavButton(UsersNavItem.USERS, selectedRoute == UsersNavItem.USERS, onUsers)
        BottomNavButton(UsersNavItem.PROFILE, selectedRoute == UsersNavItem.PROFILE, onProfile)
    }
}

@Composable
private fun BottomNavButton(
    item: UsersNavItem,
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
            colorFilter = if (!selected && item == UsersNavItem.BOARDS) {
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

private fun Modifier.dashedBorder(color: Color, radius: androidx.compose.ui.unit.Dp): Modifier {
    return drawBehind {
        val strokeWidth = 1.dp.toPx()
        drawRoundRect(
            color = color,
            size = size,
            cornerRadius = CornerRadius(radius.toPx(), radius.toPx()),
            style = Stroke(
                width = strokeWidth,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 9f), 0f)
            )
        )
    }
}

private sealed interface UserSheetMode {
    data object Create : UserSheetMode
    data class Edit(val user: UserListItem) : UserSheetMode
}

private data class UserListItem(
    val id: Int,
    val fullName: String,
    val email: String,
    val assignedTasks: List<String>,
    val taskCount: Int,
    val notes: String,
    val avatarUrl: String?
)

data class UserForm(
    val fullName: String,
    val email: String,
    val tasks: List<String>,
    val notes: String
)
