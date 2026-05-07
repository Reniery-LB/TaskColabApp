package com.taskcolab.app.feature.boards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import com.taskcolab.app.domain.model.UserRole

@Composable
fun BoardsPlaceholderScreen(
    onNavigateToBoards: () -> Unit = {},
    onNavigateToTasks: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToUsers: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val users = remember {
        listOf(
            User(1, "Reniery Lucero Beltran", "reniery@taskcolab.com", UserRole.ADMIN, true),
            User(2, "Keyra Grijalva Ochoa", "keyra@taskcolab.com", UserRole.USER, true),
            User(3, "Zahir Diaz Barrera", "zahir@taskcolab.com", UserRole.USER, true)
        )
    }
    val cards = remember {
        mutableStateListOf(
            TaskCard(
                id = 1,
                title = "Disenar Mockups Figma",
                description = "Crear la version movil de todos los modulos del sistema.",
                status = TaskStatus.PENDING,
                priority = TaskPriority.HIGH,
                dueDate = "28-08-12",
                assignedUser = users.first()
            )
        )
    }

    var selectedStatus by remember { mutableStateOf(TaskStatus.PENDING) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var cardToMove by remember { mutableStateOf<TaskCard?>(null) }
    var nextId by remember { mutableIntStateOf(2) }

    Scaffold(
        containerColor = TaskColabWhite,
        topBar = {
            BoardsHeader(
                selectedStatus = selectedStatus,
                onStatusSelected = { selectedStatus = it }
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = TaskColabBlue,
                contentColor = TaskColabWhite,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Anadir tarjeta")
            }
        }
    ) { innerPadding ->
        val visibleCards = cards.filter { it.status == selectedStatus }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 36.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(visibleCards, key = { it.id }) { card ->
                BoardTaskCard(
                    card = card,
                    onMove = { cardToMove = card },
                    onDelete = { cards.remove(card) }
                )
            }

            if (visibleCards.isEmpty()) {
                item {
                    EmptyBoardState(status = selectedStatus)
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateCardDialog(
            users = users,
            initialStatus = selectedStatus,
            onDismiss = { showCreateDialog = false },
            onCreate = { title, description, status, priority, dueDate, user ->
                cards.add(
                    TaskCard(
                        id = nextId++,
                        title = title,
                        description = description,
                        status = status,
                        priority = priority,
                        dueDate = dueDate,
                        assignedUser = user
                    )
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
                val index = cards.indexOfFirst { it.id == card.id }
                if (index >= 0) {
                    cards[index] = card.copy(status = status)
                    selectedStatus = status
                }
                cardToMove = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoardsHeader(
    selectedStatus: TaskStatus,
    onStatusSelected: (TaskStatus) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TaskColabBlue)
                .padding(start = 48.dp, top = 46.dp, end = 22.dp, bottom = 26.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis Tableros",
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
                Text(
                    text = "RC",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TaskColabInk,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TaskColabWhite)
                .padding(horizontal = 36.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatusTab(
                status = TaskStatus.PENDING,
                selected = selectedStatus == TaskStatus.PENDING,
                onClick = { onStatusSelected(TaskStatus.PENDING) }
            )
            StatusTab(
                status = TaskStatus.IN_PROGRESS,
                selected = selectedStatus == TaskStatus.IN_PROGRESS,
                onClick = { onStatusSelected(TaskStatus.IN_PROGRESS) }
            )
            StatusTab(
                status = TaskStatus.COMPLETED,
                selected = selectedStatus == TaskStatus.COMPLETED,
                onClick = { onStatusSelected(TaskStatus.COMPLETED) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusTab(
    status: TaskStatus,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = status.boardLabel(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        },
        shape = RoundedCornerShape(50),
        border = null,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color(0xFFE0E0E0),
            labelColor = Color(0xFF666666),
            selectedContainerColor = TaskColabBlue,
            selectedLabelColor = TaskColabWhite
        )
    )
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
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PriorityBadge(priority = card.priority)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = card.dueDate,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF626262),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = TaskColabBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = card.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = card.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))
            ThinLine()
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Asignado:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE9F0FF), RoundedCornerShape(3.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = card.assignedUser?.fullName.orEmpty(),
                        style = MaterialTheme.typography.labelMedium,
                        color = TaskColabBlue,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            ThinLine()
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onMove,
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TaskColabBlue),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "Mover",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TaskColabWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = TaskColabWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFFFDADA), RoundedCornerShape(4.dp))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar tarjeta",
                        tint = Color(0xFFE02020),
                        modifier = Modifier.size(22.dp)
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
            .padding(horizontal = 6.dp, vertical = 5.dp)
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
private fun EmptyBoardState(status: TaskStatus) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sin tarjetas en ${status.boardLabel()}",
            style = MaterialTheme.typography.titleMedium,
            color = TaskColabInk
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Toca + para crear una tarjeta en esta columna.",
            style = MaterialTheme.typography.bodyMedium,
            color = TaskColabMuted
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateCardDialog(
    users: List<User>,
    initialStatus: TaskStatus,
    onDismiss: () -> Unit,
    onCreate: (String, String, TaskStatus, TaskPriority, String, User?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(initialStatus) }
    var selectedPriority by remember { mutableStateOf(TaskPriority.HIGH) }
    var selectedUser by remember { mutableStateOf(users.firstOrNull()) }
    val titleError = title.isBlank()
    val descriptionError = description.isBlank()
    val dueDateError = dueDate.isBlank()
    val canCreate = !titleError && !descriptionError && !dueDateError && selectedUser != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Anadir tarjeta", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    BoardTextField(
                        value = title,
                        onValueChange = { title = it.take(60) },
                        label = "Titulo de la tarea",
                        isError = titleError
                    )
                }
                item {
                    BoardTextField(
                        value = description,
                        onValueChange = { description = it.take(160) },
                        label = "Descripcion detallada",
                        isError = descriptionError,
                        minLines = 2
                    )
                }
                item {
                    BoardTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it.take(10) },
                        label = "Fecha limite",
                        placeholder = "dd-mm-aa",
                        isError = dueDateError
                    )
                }
                item {
                    DialogChoiceSection(title = "Estado") {
                        TaskStatus.entries.forEach { status ->
                            ChoicePill(
                                label = status.boardLabel(),
                                selected = selectedStatus == status,
                                onClick = { selectedStatus = status }
                            )
                        }
                    }
                }
                item {
                    DialogChoiceSection(title = "Prioridad") {
                        TaskPriority.entries.forEach { priority ->
                            ChoicePill(
                                label = priority.priorityLabel(),
                                selected = selectedPriority == priority,
                                onClick = { selectedPriority = priority }
                            )
                        }
                    }
                }
                item {
                    DialogChoiceSection(title = "Asignar a") {
                        users.forEach { user ->
                            ChoicePill(
                                label = user.fullName,
                                selected = selectedUser?.id == user.id,
                                onClick = { selectedUser = user }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreate(
                        title.trim(),
                        description.trim(),
                        selectedStatus,
                        selectedPriority,
                        dueDate.trim(),
                        selectedUser
                    )
                },
                enabled = canCreate,
                colors = ButtonDefaults.buttonColors(containerColor = TaskColabBlue)
            ) {
                Text(text = "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar")
            }
        }
    )
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
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
        onDismissRequest = onDismiss,
        title = { Text(text = "Mover tarjeta", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TaskColabMuted
                )
                TaskStatus.entries.forEach { status ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = status != card.status) { onMoveTo(status) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (status == card.status) Color(0xFFE7E7E7) else TaskColabWhite,
                        border = BorderStroke(1.dp, if (status == card.status) TaskColabLine else TaskColabBlue)
                    ) {
                        Text(
                            text = status.boardLabel(),
                            modifier = Modifier.padding(14.dp),
                            color = if (status == card.status) TaskColabMuted else TaskColabBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cerrar")
            }
        }
    )
}

private enum class BoardNavItem(
    val label: String,
    val icon: ImageVector
) {
    BOARDS("Tableros", Icons.Filled.Assignment),
    TASKS("Tareas", Icons.Filled.Checklist),
    REPORTS("Reportes", Icons.Filled.Assessment),
    USERS("Usuarios", Icons.Filled.Group),
    PROFILE("Perfil", Icons.Filled.Person)
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
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = if (selected) TaskColabBlue else TaskColabWhite,
            modifier = Modifier.size(26.dp)
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

private fun TaskPriority.priorityLabel(): String = when (this) {
    TaskPriority.HIGH -> "Alta"
    TaskPriority.MEDIUM -> "Media"
    TaskPriority.LOW -> "Baja"
}
