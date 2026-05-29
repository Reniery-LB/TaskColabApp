package com.taskcolab.app.feature.tasks

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskcolab.app.R
import com.taskcolab.app.core.designsystem.component.TaskColabConfirmDialog
import com.taskcolab.app.core.designsystem.component.UserAvatar
import com.taskcolab.app.core.designsystem.theme.TaskColabBlue
import com.taskcolab.app.core.designsystem.theme.TaskColabDanger
import com.taskcolab.app.core.designsystem.theme.TaskColabInk
import com.taskcolab.app.core.designsystem.theme.TaskColabLine
import com.taskcolab.app.core.designsystem.theme.TaskColabMuted
import com.taskcolab.app.core.designsystem.theme.TaskColabWhite
import com.taskcolab.app.domain.model.TaskStatus
import com.taskcolab.app.domain.model.TaskPriority
import com.taskcolab.app.feature.boards.BoardsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun TasksPlaceholderScreen(
    onNavigateToBoards: () -> Unit = {},
    onNavigateToTasks: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToUsers: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onOpenChat: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: BoardsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tasks = uiState.cards.map { card ->
        TaskListItem(
            id = card.id,
            title = card.title,
            status = card.status,
            assignedUsers = card.assignedUsers
                .takeIf { it.isNotEmpty() }
                ?.joinToString { it.fullName }
                ?: "Sin asignar",
            initials = card.assignedUsers.firstOrNull()?.fullName?.toInitials(),
            avatarColor = Color(0xFFD9E3FF)
        )
    }
    val selectedIds = remember { mutableStateListOf<Int>() }
    var showCreateDialog by remember { mutableStateOf(false) }
    var deleteMode by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = TaskColabWhite,
        topBar = {
            TasksHeader(
                currentUserName = uiState.currentUser?.fullName.orEmpty(),
                currentUserAvatarUrl = uiState.currentUser?.avatarUrl,
                onOpenChat = onOpenChat,
                onLogout = onLogout
            )
        },
        bottomBar = {
            TaskColabBottomBar(
                selectedRoute = TasksNavItem.TASKS,
                onBoards = onNavigateToBoards,
                onTasks = onNavigateToTasks,
                onReports = onNavigateToReports,
                onUsers = onNavigateToUsers,
                onProfile = onNavigateToProfile
            )
        },
        floatingActionButton = {}
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                contentPadding = PaddingValues(top = 26.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskRowCard(
                        task = task,
                        checked = if (deleteMode) task.id in selectedIds else task.status == TaskStatus.COMPLETED,
                        deleteMode = deleteMode,
                        onCheckedChange = { checked ->
                            if (deleteMode) {
                                if (checked) {
                                    if (task.id !in selectedIds) selectedIds.add(task.id)
                                } else {
                                    selectedIds.remove(task.id)
                                }
                            } else {
                                uiState.cards.firstOrNull { it.id == task.id }?.let { card ->
                                    viewModel.moveLocalCard(
                                        card,
                                        if (checked) TaskStatus.COMPLETED else TaskStatus.PENDING
                                    )
                                }
                            }
                        }
                    )
                }

                if (tasks.isEmpty()) {
                    item {
                        EmptyTasksState()
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 30.dp, bottom = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (deleteMode) {
                    Text(
                        text = "Seleccionados: ${selectedIds.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = TaskColabInk,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(TaskColabWhite, RoundedCornerShape(50))
                            .border(BorderStroke(1.dp, TaskColabBlue), RoundedCornerShape(50))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
                FloatingActionButton(
                    onClick = {
                        if (deleteMode) {
                            if (selectedIds.isNotEmpty()) {
                                showDeleteConfirm = true
                            } else {
                                deleteMode = false
                            }
                        } else {
                            selectedIds.clear()
                            deleteMode = true
                        }
                    },
                    containerColor = TaskColabWhite,
                    contentColor = TaskColabDanger,
                    shape = CircleShape
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icn_basura),
                        contentDescription = "Eliminar seleccionadas",
                        modifier = Modifier.size(40.dp)
                    )
                }
                FloatingActionButton(
                    onClick = {
                        deleteMode = false
                        selectedIds.clear()
                        showCreateDialog = true
                    },
                    containerColor = TaskColabBlue,
                    contentColor = TaskColabWhite,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Añadir tarea",
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateTaskSheet(
            users = uiState.users,
            onDismiss = { showCreateDialog = false },
            onCreate = { title, description, status, priority, dueDate, selectedUsers ->
                viewModel.addLocalCard(
                    title = title,
                    description = description,
                    status = status,
                    priority = priority,
                    dueDate = dueDate,
                    selectedUsers = selectedUsers
                )
                showCreateDialog = false
            }
        )
    }

    if (showDeleteConfirm) {
        TaskColabConfirmDialog(
            title = "Eliminar tareas",
            message = "Se eliminarán ${selectedIds.size} tarea(s) seleccionada(s). Esta acción no se puede deshacer.",
            onConfirm = {
                uiState.cards
                    .filter { it.id in selectedIds }
                    .forEach { viewModel.deleteLocalCard(it) }
                selectedIds.clear()
                deleteMode = false
                showDeleteConfirm = false
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }
}

@Composable
private fun TasksHeader(
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
            text = "Mis Tareas",
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
private fun TaskRowCard(
    task: TaskListItem,
    checked: Boolean,
    deleteMode: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(5.dp, RoundedCornerShape(8.dp), ambientColor = Color.Black.copy(alpha = 0.12f))
            .border(BorderStroke(2.dp, TaskColabBlue), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = if (deleteMode && checked) Color(0xFFE7E7E7) else TaskColabWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCheckedChange(!checked) }
                .padding(start = 14.dp, top = 14.dp, end = 18.dp, bottom = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = TaskColabBlue,
                        uncheckedColor = Color(0xFF8E8585),
                        checkmarkColor = TaskColabWhite
                    )
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusBadge(status = task.status)
                }

                Spacer(modifier = Modifier.width(12.dp))

                if (task.initials != null && task.avatarColor != null) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(task.avatarColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = task.initials,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            TaskThinLine()
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "Asignado:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF666666),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFE9F0FF), RoundedCornerShape(3.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = task.assignedUsers,
                        style = MaterialTheme.typography.labelLarge,
                        color = TaskColabBlue,
                        overflow = TextOverflow.Clip
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            TaskThinLine()
        }
    }
}

@Composable
private fun TaskThinLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFD0D0D0))
    )
}

@Composable
private fun StatusBadge(status: TaskStatus) {
    val colors = when (status) {
        TaskStatus.PENDING -> StatusColors(
            label = "PENDIENTE",
            text = Color(0xFFFF1F1F),
            background = Color(0xFFFFE0E0)
        )
        TaskStatus.IN_PROGRESS -> StatusColors(
            label = "EN PROCESO",
            text = Color(0xFFE58600),
            background = Color(0xFFF7E7D1)
        )
        TaskStatus.COMPLETED -> StatusColors(
            label = "COMPLETADO",
            text = Color(0xFF00E016),
            background = Color(0xFFDDFCE3)
        )
    }

    Box(
        modifier = Modifier
            .width(114.dp)
            .height(24.dp)
            .background(colors.background, RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = colors.label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.text,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun EmptyTasksState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No hay tareas",
            style = MaterialTheme.typography.titleMedium,
            color = TaskColabInk
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Toca + para agregar una tarea.",
            style = MaterialTheme.typography.bodyMedium,
            color = TaskColabMuted
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTaskSheet(
    users: List<com.taskcolab.app.domain.model.User>,
    onDismiss: () -> Unit,
    onCreate: (String, String, TaskStatus, TaskPriority, String, List<com.taskcolab.app.domain.model.User>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(TaskStatus.PENDING) }
    var selectedPriority by remember { mutableStateOf(TaskPriority.HIGH) }
    val selectedUserIds = remember { mutableStateListOf<Int>() }
    var showDatePicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val canCreate = title.isNotBlank() && description.isNotBlank() && dueDate.length == 8

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TaskColabWhite,
        shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Añadir Tarea",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            LabeledTaskField(
                label = "TITULO DE LA TAREA",
                value = title,
                onValueChange = { title = it.take(60) },
                placeholder = "Ej: Diseñar Base de Datos",
                singleLine = true
            )

            LabeledTaskField(
                label = "DESCRIPCIÓN DETALLADA",
                value = description,
                onValueChange = { description = it.take(160) },
                placeholder = "Explica los puntos clave....",
                minLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ExpandableSelector(
                    label = "ESTADO",
                    value = selectedStatus.taskLabel(),
                    options = TaskStatus.entries,
                    optionLabel = { it.taskLabel() },
                    modifier = Modifier.weight(1f),
                    onSelected = { selectedStatus = it }
                )
                ExpandableSelector(
                    label = "PRIORIDAD",
                    value = selectedPriority.priorityLabel(),
                    options = TaskPriority.entries,
                    optionLabel = { it.priorityLabel() },
                    modifier = Modifier.weight(1f),
                    onSelected = { selectedPriority = it },
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "ASIGNAR",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF707070),
                    fontWeight = FontWeight.Bold
                )
                users.forEach { user ->
                    AssignmentRow(
                        name = user.fullName,
                        checked = user.id in selectedUserIds,
                        onCheckedChange = { checked ->
                            if (checked) {
                                if (user.id !in selectedUserIds) selectedUserIds.add(user.id)
                            } else {
                                selectedUserIds.remove(user.id)
                            }
                        }
                    )
                }
            }

            LabeledTaskField(
                label = "FECHA LIMITE",
                value = dueDate,
                onValueChange = { dueDate = formatShortDate(it) },
                placeholder = "dd/mm/aaaa",
                singleLine = true,
                readOnly = true,
                onClick = { showDatePicker = true },
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.calendario),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                }
            )

            SheetActionButtons(
                cancelText = "Cancelar",
                saveText = "Crear",
                canSave = canCreate,
                onCancel = onDismiss,
                onSave = {
                    onCreate(
                        title.trim(),
                        description.trim(),
                        selectedStatus,
                        selectedPriority,
                        dueDate.trim(),
                        users.filter { it.id in selectedUserIds }
                    )
                }
            )
        }
    }

    if (showDatePicker) {
        TaskDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = {
                dueDate = it
                showDatePicker = false
            }
        )
    }
}

@Composable
private fun LabeledTaskField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    minLines: Int = 1,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF707070),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        color = Color(0xFF9C9C9C),
                        fontWeight = FontWeight.Bold
                    )
                },
                trailingIcon = trailingIcon,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(8.dp), ambientColor = Color.Black.copy(alpha = 0.16f)),
                shape = RoundedCornerShape(8.dp),
                readOnly = readOnly,
                singleLine = singleLine,
                minLines = minLines,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = TaskColabBlue,
                    unfocusedBorderColor = TaskColabBlue,
                    cursorColor = TaskColabBlue,
                    focusedContainerColor = TaskColabWhite,
                    unfocusedContainerColor = TaskColabWhite
                )
            )
            if (onClick != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable(onClick = onClick)
                )
            }
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
private fun <T> ExpandableSelector(
    label: String,
    value: String,
    options: List<T>,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier,
    onSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF707070),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .border(BorderStroke(1.dp, TaskColabBlue), RoundedCornerShape(8.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(
                    id = if (expanded) R.drawable.icn_flecha_arriba else R.drawable.icn_flecha_abajo
                ),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier.size(16.dp)
            )
        }
        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .border(BorderStroke(1.dp, TaskColabBlue), RoundedCornerShape(8.dp))
                    .background(TaskColabWhite, RoundedCornerShape(8.dp))
            ) {
                options.forEach { option ->
                    Text(
                        text = optionLabel(option),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelected(option)
                                expanded = false
                            }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AssignmentRow(
    name: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(if (checked) Color(0xFFE7E7E7) else TaskColabWhite, RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, TaskColabBlue), RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(28.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = TaskColabBlue,
                uncheckedColor = Color(0xFF8E8585),
                checkmarkColor = TaskColabWhite
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TaskTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TaskColabBlue,
            unfocusedBorderColor = TaskColabLine,
            cursorColor = TaskColabBlue
        )
    )
}

@Composable
private fun DialogChoiceSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = TaskColabInk,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun ChoicePill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) TaskColabBlue else Color(0xFFE7E7E7),
                shape = RoundedCornerShape(50)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) TaskColabWhite else Color(0xFF5F5F5F),
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun ColorChoice(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .border(
                BorderStroke(if (selected) 3.dp else 1.dp, if (selected) TaskColabBlue else TaskColabLine),
                CircleShape
            )
            .padding(4.dp)
            .background(color, CircleShape)
            .clickable(onClick = onClick)
    )
}

private enum class TasksNavItem(
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
    selectedRoute: TasksNavItem,
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
        BottomNavButton(TasksNavItem.BOARDS, selectedRoute == TasksNavItem.BOARDS, onBoards)
        BottomNavButton(TasksNavItem.TASKS, selectedRoute == TasksNavItem.TASKS, onTasks)
        BottomNavButton(TasksNavItem.REPORTS, selectedRoute == TasksNavItem.REPORTS, onReports)
        BottomNavButton(TasksNavItem.USERS, selectedRoute == TasksNavItem.USERS, onUsers)
        BottomNavButton(TasksNavItem.PROFILE, selectedRoute == TasksNavItem.PROFILE, onProfile)
    }
}

@Composable
private fun BottomNavButton(
    item: TasksNavItem,
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
            colorFilter = if (!selected && item == TasksNavItem.BOARDS) {
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

private data class TaskListItem(
    val id: Int,
    val title: String,
    val status: TaskStatus,
    val assignedUsers: String,
    val initials: String?,
    val avatarColor: Color?
)

private data class StatusColors(
    val label: String,
    val text: Color,
    val background: Color
)

private fun TaskStatus.taskLabel(): String = when (this) {
    TaskStatus.PENDING -> "Pendiente"
    TaskStatus.IN_PROGRESS -> "En proceso"
    TaskStatus.COMPLETED -> "Completado"
}

private fun TaskPriority.priorityLabel(): String = when (this) {
    TaskPriority.HIGH -> "Alta"
    TaskPriority.MEDIUM -> "Media"
    TaskPriority.LOW -> "Baja"
}

private fun String.toInitials(): String =
    trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString(separator = "") { it.first().uppercase() }
        .ifBlank { "TC" }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(formatDateMillis(millis))
                    }
                }
            ) {
                Text(text = "Aceptar", color = TaskColabBlue, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar", color = TaskColabBlue)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

private fun formatShortDate(rawValue: String): String {
    val digits = rawValue.filter { it.isDigit() }.take(6)
    return buildString {
        digits.forEachIndexed { index, char ->
            if (index == 2 || index == 4) append('-')
            append(char)
        }
    }
}

private fun formatDateMillis(millis: Long): String {
    return SimpleDateFormat("dd-MM-yy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date(millis))
}
