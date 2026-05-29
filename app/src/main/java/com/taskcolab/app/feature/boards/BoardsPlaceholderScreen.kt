package com.taskcolab.app.feature.boards

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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskcolab.app.R
import com.taskcolab.app.core.designsystem.component.TaskColabConfirmDialog
import com.taskcolab.app.core.designsystem.component.UserAvatar
import com.taskcolab.app.core.designsystem.theme.TaskColabBlue
import com.taskcolab.app.core.designsystem.theme.TaskColabDanger
import com.taskcolab.app.core.designsystem.theme.TaskColabInk
import com.taskcolab.app.core.designsystem.theme.TaskColabLine
import com.taskcolab.app.core.designsystem.theme.TaskColabMuted
import com.taskcolab.app.core.designsystem.theme.TaskColabWhite
import com.taskcolab.app.domain.model.TaskCard
import com.taskcolab.app.domain.model.TaskPriority
import com.taskcolab.app.domain.model.TaskStatus
import com.taskcolab.app.domain.model.User
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun BoardsPlaceholderScreen(
    onNavigateToBoards: () -> Unit = {},
    onNavigateToProjects: () -> Unit = {},
    onNavigateToTasks: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToUsers: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onOpenChat: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: BoardsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedStatus by remember { mutableStateOf(TaskStatus.PENDING) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var cardToMove by remember { mutableStateOf<TaskCard?>(null) }
    var cardToDelete by remember { mutableStateOf<TaskCard?>(null) }

    Scaffold(
        containerColor = TaskColabWhite,
        topBar = {
            BoardsHeader(
                projectName = uiState.activeProject?.name ?: "Proyecto general",
                selectedStatus = selectedStatus,
                currentUser = uiState.currentUser,
                onStatusSelected = { selectedStatus = it },
                onNavigateToProjects = onNavigateToProjects,
                onOpenChat = onOpenChat,
                onLogout = onLogout
            )
        },
        bottomBar = {
            TaskColabBottomBar(
                selectedRoute = BoardNavItem.BOARDS,
                onBoards = onNavigateToBoards,
                onTasks = onNavigateToTasks,
                onReports = onNavigateToReports,
                onUsers = onNavigateToUsers,
                onProfile = onNavigateToProfile
            )
        },
        floatingActionButton = {}
    ) { innerPadding ->
        val visibleCards = uiState.cards.filter { it.status == selectedStatus }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                contentPadding = PaddingValues(top = 20.dp, bottom = 104.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(visibleCards, key = { it.id }) { card ->
                    BoardTaskCard(
                        card = card,
                        onMove = { cardToMove = card },
                        onDelete = { cardToDelete = card }
                    )
                }

                if (visibleCards.isEmpty()) {
                    item {
                        EmptyBoardState(
                            status = selectedStatus,
                            isLoading = uiState.isLoading,
                            error = uiState.error
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = TaskColabBlue,
                contentColor = TaskColabWhite,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 30.dp, bottom = 18.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Añadir tarjeta",
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateCardSheet(
            users = uiState.users,
            initialStatus = selectedStatus,
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
                selectedStatus = status
                showCreateDialog = false
            }
        )
    }

    cardToMove?.let { card ->
        MoveCardDialog(
            card = card,
            onDismiss = { cardToMove = null },
            onMoveTo = { status ->
                viewModel.moveLocalCard(card, status)
                selectedStatus = status
                cardToMove = null
            }
        )
    }

    cardToDelete?.let { card ->
        TaskColabConfirmDialog(
            title = "Eliminar tarjeta",
            message = "La tarjeta se eliminará del tablero y no aparecerá en tus tareas.",
            onConfirm = {
                viewModel.deleteLocalCard(card)
                cardToDelete = null
            },
            onDismiss = { cardToDelete = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoardsHeader(
    projectName: String,
    selectedStatus: TaskStatus,
    currentUser: User?,
    onStatusSelected: (TaskStatus) -> Unit,
    onNavigateToProjects: () -> Unit,
    onOpenChat: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TaskColabBlue)
                .padding(start = 18.dp, top = 46.dp, end = 22.dp, bottom = 26.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tablero",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TaskColabWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = projectName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TaskColabWhite.copy(alpha = 0.88f)
                )
            }
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
                fullName = currentUser?.fullName.orEmpty(),
                avatarUrl = currentUser?.avatarUrl,
                size = 60.dp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(TaskColabWhite)
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onNavigateToProjects,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TaskColabBlue, contentColor = TaskColabWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text("Proyectos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusTab(
                    status = TaskStatus.PENDING,
                    selected = selectedStatus == TaskStatus.PENDING,
                    onClick = { onStatusSelected(TaskStatus.PENDING) },
                    modifier = Modifier.weight(1f)
                )
                StatusTab(
                    status = TaskStatus.IN_PROGRESS,
                    selected = selectedStatus == TaskStatus.IN_PROGRESS,
                    onClick = { onStatusSelected(TaskStatus.IN_PROGRESS) },
                    modifier = Modifier.weight(1f)
                )
                StatusTab(
                    status = TaskStatus.COMPLETED,
                    selected = selectedStatus == TaskStatus.COMPLETED,
                    onClick = { onStatusSelected(TaskStatus.COMPLETED) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusTab(
    status: TaskStatus,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .background(
                color = if (selected) TaskColabBlue else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(50)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.boardLabel(),
            color = if (selected) TaskColabWhite else Color(0xFF666666),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun BoardTaskCard(
    card: TaskCard,
    onMove: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(8.dp), ambientColor = Color.Black.copy(alpha = 0.14f))
            .border(BorderStroke(2.dp, TaskColabBlue), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = TaskColabWhite
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PriorityBadge(priority = card.priority)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = card.dueDate,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF626262),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.calendario),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = card.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = card.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF666666)
            )

            Spacer(modifier = Modifier.height(14.dp))
            ThinLine()
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
                        text = card.assignedUsers
                            .takeIf { it.isNotEmpty() }
                            ?.joinToString { it.fullName }
                        ?: "Sin asignar",
                        style = MaterialTheme.typography.labelLarge,
                        color = TaskColabBlue,
                        overflow = TextOverflow.Clip
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            ThinLine()
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onMove,
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TaskColabBlue),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(
                        text = "Mover",
                        style = MaterialTheme.typography.titleMedium,
                        color = TaskColabWhite,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .width(56.dp)
                        .height(40.dp)
                        .background(Color(0xFFFFDADA), RoundedCornerShape(5.dp))
                        .clickable(onClick = onDelete),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.btn_basura),
                        contentDescription = "Eliminar tarjeta",
                        modifier = Modifier.size(42.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PriorityBadge(priority: TaskPriority) {
    val (label, textColor, backgroundColor) = when (priority) {
        TaskPriority.HIGH -> Triple("ALTA PRIORIDAD", Color(0xFFD71920), Color(0xFFFFE1E1))
        TaskPriority.MEDIUM -> Triple("MEDIA PRIORIDAD", Color(0xFFB36B00), Color(0xFFFFEECF))
        TaskPriority.LOW -> Triple("BAJA PRIORIDAD", Color(0xFF16883A), Color(0xFFE0F7E7))
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(3.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ThinLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFD0D0D0))
    )
}

@Composable
private fun EmptyBoardState(
    status: TaskStatus,
    isLoading: Boolean,
    error: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when {
                isLoading -> "Cargando tareas..."
                error != null -> "No se pudieron cargar tareas"
                else -> "Sin tarjetas en ${status.boardLabel()}"
            },
            style = MaterialTheme.typography.titleMedium,
            color = TaskColabInk
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error ?: "Toca + para crear una tarjeta en esta columna.",
            style = MaterialTheme.typography.bodyMedium,
            color = TaskColabMuted
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateCardSheet(
    users: List<User>,
    initialStatus: TaskStatus,
    onDismiss: () -> Unit,
    onCreate: (String, String, TaskStatus, TaskPriority, String, List<User>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(initialStatus) }
    var selectedPriority by remember { mutableStateOf(TaskPriority.HIGH) }
    val selectedUserIds = remember { mutableStateListOf<Int>() }
    var showDatePicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val titleError = title.isBlank()
    val descriptionError = description.isBlank()
    val canCreate = !titleError && !descriptionError && dueDate.length == 8

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
                text = "Añadir Tarjeta",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            LabeledBoardField(
                label = "TITULO DE LA TAREA",
                value = title,
                onValueChange = { title = it.take(60) },
                placeholder = "Ej: Diseñar Base de Datos",
                singleLine = true
            )

            LabeledBoardField(
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
                ExpandableBoardSelector(
                    label = "ESTADO",
                    value = selectedStatus.boardLabel(),
                    options = TaskStatus.entries,
                    optionLabel = { it.boardLabel() },
                    modifier = Modifier.weight(1f),
                    onSelected = { selectedStatus = it }
                )
                ExpandableBoardSelector(
                    label = "PRIORIDAD",
                    value = selectedPriority.priorityLabel(),
                    options = TaskPriority.entries,
                    optionLabel = { it.priorityLabel() },
                    modifier = Modifier.weight(1f),
                    onSelected = { selectedPriority = it }
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "ASIGNAR",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF707070),
                    fontWeight = FontWeight.Bold
                )
                users.forEach { user ->
                    UserChoiceRow(
                        name = user.fullName,
                        selected = user.id in selectedUserIds,
                        onClick = {
                            if (user.id in selectedUserIds) {
                                selectedUserIds.remove(user.id)
                            } else {
                                selectedUserIds.add(user.id)
                            }
                        }
                    )
                }
            }

            LabeledBoardField(
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
        BoardDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = {
                dueDate = it
                showDatePicker = false
            }
        )
    }
}

@Composable
private fun LabeledBoardField(
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
private fun <T> ExpandableBoardSelector(
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
private fun UserChoiceRow(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(if (selected) Color(0xFFE7E7E7) else TaskColabWhite, RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, TaskColabBlue), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
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
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = modifier.fillMaxWidth(),
        isError = isError,
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TaskColabBlue,
            unfocusedBorderColor = TaskColabLine,
            errorBorderColor = TaskColabDanger,
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
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MoveCardDialog(
    card: TaskCard,
    onDismiss: () -> Unit,
    onMoveTo: (TaskStatus) -> Unit
) {
    AlertDialog(
        modifier = Modifier.border(BorderStroke(2.dp, TaskColabBlue), RoundedCornerShape(28.dp)),
        onDismissRequest = onDismiss,
        containerColor = TaskColabWhite,
        shape = RoundedCornerShape(28.dp),
        title = { Text(text = "Mover Tarjeta", color = Color.Black, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(26.dp)) {
                TaskStatus.entries.forEach { status ->
                    val (backgroundColor, textColor, borderColor) = status.moveOptionColors(card.status)
                    Surface(
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth()
                            .clickable(enabled = status != card.status) { onMoveTo(status) },
                        shape = RoundedCornerShape(8.dp),
                        color = backgroundColor,
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Text(
                            text = status.boardLabel(),
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 17.dp),
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cerrar", color = TaskColabBlue, fontWeight = FontWeight.Bold)
            }
        }
    )
}

private enum class BoardNavItem(
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
    selectedRoute: BoardNavItem,
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
        BottomNavButton(BoardNavItem.BOARDS, selectedRoute == BoardNavItem.BOARDS, onBoards)
        BottomNavButton(BoardNavItem.TASKS, selectedRoute == BoardNavItem.TASKS, onTasks)
        BottomNavButton(BoardNavItem.REPORTS, selectedRoute == BoardNavItem.REPORTS, onReports)
        BottomNavButton(BoardNavItem.USERS, selectedRoute == BoardNavItem.USERS, onUsers)
        BottomNavButton(BoardNavItem.PROFILE, selectedRoute == BoardNavItem.PROFILE, onProfile)
    }
}

@Composable
private fun BottomNavButton(
    item: BoardNavItem,
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
            colorFilter = if (!selected && item == BoardNavItem.BOARDS) {
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

private fun TaskStatus.boardLabel(): String = when (this) {
    TaskStatus.PENDING -> "Pendiente"
    TaskStatus.IN_PROGRESS -> "En Proceso"
    TaskStatus.COMPLETED -> "Completado"
}

private fun TaskStatus.moveOptionColors(currentStatus: TaskStatus): Triple<Color, Color, Color> = when {
    this == TaskStatus.PENDING -> Triple(Color(0xFFFFE1E1), Color(0xFFD71920), TaskColabBlue)
    this == currentStatus -> Triple(Color(0xFFEFEFEF), Color(0xFF6B6B6B), TaskColabBlue)
    this == TaskStatus.IN_PROGRESS -> Triple(Color(0xFFFFEBD2), Color(0xFFE88A00), TaskColabBlue)
    else -> Triple(Color(0xFFE0FFE6), Color(0xFF00D615), TaskColabBlue)
}

private fun TaskPriority.priorityLabel(): String = when (this) {
    TaskPriority.HIGH -> "Alta"
    TaskPriority.MEDIUM -> "Media"
    TaskPriority.LOW -> "Baja"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoardDatePickerDialog(
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
