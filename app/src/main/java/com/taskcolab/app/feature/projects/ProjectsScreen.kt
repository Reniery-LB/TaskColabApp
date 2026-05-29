package com.taskcolab.app.feature.projects

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskcolab.app.core.designsystem.component.TaskColabConfirmDialog
import com.taskcolab.app.core.designsystem.theme.TaskColabBlue
import com.taskcolab.app.core.designsystem.theme.TaskColabDanger
import com.taskcolab.app.core.designsystem.theme.TaskColabInk
import com.taskcolab.app.core.designsystem.theme.TaskColabMuted
import com.taskcolab.app.core.designsystem.theme.TaskColabWhite
import com.taskcolab.app.domain.model.Project
import com.taskcolab.app.domain.model.ProjectStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun ProjectsScreen(
    onOpenBoard: () -> Unit,
    onOpenChat: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProjectsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateSheet by remember { mutableStateOf(false) }
    var projectToArchive by remember { mutableStateOf<Project?>(null) }
    var projectToDelete by remember { mutableStateOf<Project?>(null) }

    Scaffold(
        containerColor = TaskColabWhite,
        topBar = { ProjectsHeader(onOpenChat = onOpenChat, onLogout = onLogout) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateSheet = true },
                containerColor = TaskColabBlue,
                contentColor = TaskColabWhite,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Crear proyecto")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 18.dp, top = 18.dp, end = 18.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (uiState.isLoading || (uiState.error != null && uiState.projects.isEmpty()) || uiState.projects.isEmpty()) {
                item {
                    StateMessage(uiState.isLoading, uiState.error)
                }
            }

            items(uiState.projects, key = { it.id }) { project ->
                ProjectCard(
                    project = project,
                    selected = uiState.activeProject?.id == project.id,
                    onSelect = { viewModel.selectProject(project) },
                    onOpenBoard = {
                        viewModel.selectProject(project)
                        onOpenBoard()
                    },
                    onOpenChat = {
                        viewModel.selectProject(project)
                        onOpenChat()
                    },
                    onPause = { viewModel.pauseProject(project) },
                    onArchive = { projectToArchive = project },
                    onRestore = {},
                    onDelete = {}
                )
            }

            item {
                SectionTitle(
                    title = "Proyectos archivados",
                    subtitle = if (uiState.archivedProjects.isEmpty()) {
                        "No hay proyectos archivados"
                    } else {
                        "Puedes desarchivar o eliminar"
                    }
                )
            }

            items(uiState.archivedProjects, key = { "archived-${it.id}" }) { project ->
                ProjectCard(
                    project = project,
                    selected = false,
                    onSelect = {},
                    onOpenBoard = {},
                    onOpenChat = {},
                    onPause = {},
                    onArchive = {},
                    onRestore = { viewModel.restoreProject(project) },
                    onDelete = { projectToDelete = project }
                )
            }
        }
    }

    if (showCreateSheet) {
        CreateProjectSheet(
            onDismiss = { showCreateSheet = false },
            onCreate = { name, description, dueDate, color ->
                viewModel.createProject(name, description, color, dueDate.ifBlank { null })
                showCreateSheet = false
            }
        )
    }

    projectToArchive?.let { project ->
        TaskColabConfirmDialog(
            title = "Archivar proyecto",
            message = "El proyecto se moverá a archivados y dejará de aparecer como activo.",
            confirmText = "Archivar",
            onConfirm = {
                viewModel.archiveProject(project)
                projectToArchive = null
            },
            onDismiss = { projectToArchive = null }
        )
    }

    projectToDelete?.let { project ->
        TaskColabConfirmDialog(
            title = "Eliminar proyecto",
            message = "Esta acción eliminará el proyecto archivado de forma permanente.",
            onConfirm = {
                viewModel.deleteArchivedProject(project)
                projectToDelete = null
            },
            onDismiss = { projectToDelete = null }
        )
    }
}

@Composable
private fun ProjectsHeader(
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Proyectos",
                style = MaterialTheme.typography.headlineLarge,
                color = TaskColabWhite,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Selecciona el espacio activo",
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
    }
}

@Composable
private fun ProjectCard(
    project: Project,
    selected: Boolean,
    onSelect: () -> Unit,
    onOpenBoard: () -> Unit,
    onOpenChat: () -> Unit,
    onPause: () -> Unit,
    onArchive: () -> Unit,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    val archived = project.status == ProjectStatus.ARCHIVED
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(5.dp, RoundedCornerShape(8.dp), ambientColor = Color.Black.copy(alpha = 0.12f))
            .border(BorderStroke(if (selected) 2.dp else 1.dp, TaskColabBlue), RoundedCornerShape(8.dp))
            .clickable(enabled = !archived, onClick = onSelect),
        shape = RoundedCornerShape(8.dp),
        color = TaskColabWhite
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(project.color.toColor(), CircleShape)
                )
                Spacer(modifier = Modifier.size(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = project.description.ifBlank { "Sin descripción" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TaskColabMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                FilterChip(
                    selected = selected,
                    onClick = { if (!archived) onSelect() },
                    label = { Text(if (selected) "Activo" else project.status.label()) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TaskColabBlue,
                        selectedLabelColor = TaskColabWhite
                    )
                )
            }

            ProjectProgress(project)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (archived) {
                    ProjectAction("Desarchivar", Icons.Filled.Unarchive, onRestore, Modifier.weight(1f))
                    ProjectAction("Eliminar", Icons.Filled.Delete, onDelete, Modifier.weight(1f), TaskColabDanger)
                } else {
                    ProjectAction("Tablero", Icons.Filled.ViewKanban, onOpenBoard, Modifier.weight(1f))
                    ProjectAction("Chat", Icons.Filled.Chat, onOpenChat, Modifier.weight(1f))
                    IconButton(onClick = onPause) {
                        Icon(
                            imageVector = if (project.status == ProjectStatus.PAUSED) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                            contentDescription = "Pausar proyecto",
                            tint = TaskColabBlue
                        )
                    }
                    IconButton(onClick = onArchive) {
                        Icon(Icons.Filled.Archive, contentDescription = "Archivar proyecto", tint = TaskColabDanger)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TaskColabInk,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TaskColabMuted
        )
    }
}

@Composable
private fun ProjectProgress(project: Project) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${project.progress}% completado",
                style = MaterialTheme.typography.bodyMedium,
                color = TaskColabInk,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${project.doneTasks}/${project.totalTasks} tareas",
                style = MaterialTheme.typography.bodyMedium,
                color = TaskColabMuted
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .background(Color(0xFFD9D9D9), RoundedCornerShape(50))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(project.progress.coerceIn(0, 100) / 100f)
                    .height(9.dp)
                    .background(TaskColabBlue, RoundedCornerShape(50))
            )
        }
        Text(
            text = "Pendientes ${project.pendingTasks} · En proceso ${project.inProgressTasks} · Miembros ${project.membersCount}",
            style = MaterialTheme.typography.labelMedium,
            color = TaskColabMuted
        )
    }
}

@Composable
private fun ProjectAction(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = TaskColabBlue
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = TaskColabWhite
        ),
        contentPadding = PaddingValues(horizontal = 10.dp),
        modifier = modifier.height(42.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.size(6.dp))
        Text(label, maxLines = 1)
    }
}

@Composable
private fun StateMessage(isLoading: Boolean, error: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when {
                isLoading -> "Cargando proyectos..."
                error != null -> "No se pudieron cargar proyectos"
                else -> "Aún no hay proyectos"
            },
            style = MaterialTheme.typography.titleMedium,
            color = TaskColabInk,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = error ?: "Crea tu primer proyecto para organizar tablero, tareas y chat.",
            style = MaterialTheme.typography.bodyMedium,
            color = TaskColabMuted
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateProjectSheet(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("#1B5CFF") }
    var showDatePicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val canCreate = name.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TaskColabWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Nuevo Proyecto",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            LabeledProjectField(
                label = "Nombre",
                value = name,
                onValueChange = { name = it.take(80) },
                placeholder = "Ej: Rediseño TaskColab Web",
                singleLine = true
            )
            LabeledProjectField(
                label = "Descripción",
                value = description,
                onValueChange = { description = it.take(180) },
                placeholder = "Objetivo, alcance o entregables principales",
                minLines = 4
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabeledProjectField(
                    label = "Fecha objetivo",
                    value = dueDate,
                    onValueChange = {},
                    placeholder = "dd/mm/aaaa",
                    singleLine = true,
                    readOnly = true,
                    onClick = { showDatePicker = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = TaskColabBlue
                        )
                    },
                    modifier = Modifier.weight(1.35f)
                )
                ColorProjectField(
                    value = color,
                    onValueChange = { color = normalizeProjectColor(it) },
                    modifier = Modifier.weight(0.9f)
                )
            }
            SheetActionButtons(
                cancelText = "Cancelar",
                saveText = "Crear",
                canSave = canCreate,
                onCancel = onDismiss,
                onSave = { onCreate(name.trim(), description.trim(), dueDate.trim(), color.trim()) }
            )
        }
    }

    if (showDatePicker) {
        ProjectDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = {
                dueDate = it
                showDatePicker = false
            }
        )
    }
}

@Composable
private fun LabeledProjectField(
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
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, color = Color(0xFF9C9C9C)) },
                trailingIcon = trailingIcon,
                minLines = minLines,
                singleLine = singleLine,
                readOnly = readOnly,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = TaskColabBlue,
                    unfocusedBorderColor = Color(0xFF9AB1FF),
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
private fun ColorProjectField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPalette by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(
            text = "Color",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(BorderStroke(1.dp, Color(0xFF9AB1FF)), RoundedCornerShape(8.dp))
                .clickable { showPalette = true }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(value.toColor(), RoundedCornerShape(3.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value.uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (showPalette) {
        ProjectColorPaletteDialog(
            selectedColor = value,
            onDismiss = { showPalette = false },
            onSelected = {
                onValueChange(it)
                showPalette = false
            }
        )
    }
}

@Composable
private fun ProjectColorPaletteDialog(
    selectedColor: String,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit
) {
    val colors = listOf(
        "#1B5CFF", "#00A6A6", "#22A95A", "#F2A900",
        "#E03E3E", "#8B5CF6", "#111827", "#6B7280",
        "#EC4899", "#14B8A6", "#F97316", "#0EA5E9"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TaskColabWhite,
        title = {
            Text("Elige un color", color = TaskColabInk, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                colors.chunked(4).forEach { rowColors ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowColors.forEach { option ->
                            val selected = option.equals(selectedColor, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .border(
                                        BorderStroke(if (selected) 3.dp else 1.dp, if (selected) TaskColabBlue else Color(0xFFD7DCEB)),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .background(option.toColor(), RoundedCornerShape(8.dp))
                                    .clickable { onSelected(option) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = TaskColabBlue, fontWeight = FontWeight.Bold)
            }
        }
    )
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
            Text(cancelText, color = TaskColabBlue, fontWeight = FontWeight.Bold)
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
            Text(saveText, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectDatePickerDialog(
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

private fun normalizeProjectColor(rawValue: String): String {
    val cleaned = rawValue.uppercase().filterIndexed { index, char ->
        char in '0'..'9' || char in 'A'..'F' || (index == 0 && char == '#')
    }
    val withHash = if (cleaned.startsWith("#")) cleaned else "#$cleaned"
    return withHash.take(7)
}

private fun ProjectStatus.label(): String = when (this) {
    ProjectStatus.ACTIVE -> "Activo"
    ProjectStatus.PAUSED -> "Pausado"
    ProjectStatus.ARCHIVED -> "Archivado"
}

private fun String.toColor(): Color =
    runCatching { Color(android.graphics.Color.parseColor(this)) }.getOrDefault(TaskColabBlue)

private fun formatDateMillis(millis: Long): String {
    return SimpleDateFormat("dd-MM-yy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date(millis))
}
