package com.taskcolab.app.feature.projects

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.taskcolab.app.core.designsystem.theme.TaskColabBlue
import com.taskcolab.app.core.designsystem.theme.TaskColabDanger
import com.taskcolab.app.core.designsystem.theme.TaskColabInk
import com.taskcolab.app.core.designsystem.theme.TaskColabMuted
import com.taskcolab.app.core.designsystem.theme.TaskColabWhite
import com.taskcolab.app.domain.model.Project
import com.taskcolab.app.domain.model.ProjectStatus

@Composable
fun ProjectsScreen(
    onOpenBoard: () -> Unit,
    onOpenChat: () -> Unit,
    viewModel: ProjectsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = TaskColabWhite,
        topBar = { ProjectsHeader() },
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
            if (uiState.isLoading || uiState.error != null || uiState.projects.isEmpty()) {
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
                    onArchive = { viewModel.archiveProject(project) }
                )
            }
        }
    }

    if (showCreateSheet) {
        CreateProjectSheet(
            onDismiss = { showCreateSheet = false },
            onCreate = { name, description, color ->
                viewModel.createProject(name, description, color, null)
                showCreateSheet = false
            }
        )
    }
}

@Composable
private fun ProjectsHeader() {
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
    onArchive: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(5.dp, RoundedCornerShape(8.dp), ambientColor = Color.Black.copy(alpha = 0.12f))
            .border(BorderStroke(if (selected) 2.dp else 1.dp, TaskColabBlue), RoundedCornerShape(8.dp))
            .clickable(onClick = onSelect),
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
                    onClick = onSelect,
                    label = { Text(if (selected) "Activo" else project.status.label()) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TaskColabBlue,
                        selectedLabelColor = TaskColabWhite
                    )
                )
            }

            ProjectProgress(project)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = TaskColabBlue),
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
    onCreate: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("#1B5CFF") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TaskColabWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Nuevo proyecto", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            ProjectField(name, { name = it.take(80) }, "Nombre")
            ProjectField(description, { description = it.take(180) }, "Descripción", minLines = 3)
            ProjectField(color, { color = it.take(7) }, "Color hexadecimal")
            Button(
                onClick = { onCreate(name.trim(), description.trim(), color.trim()) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = TaskColabBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Crear", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ProjectField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        minLines = minLines,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TaskColabBlue,
            unfocusedBorderColor = Color(0xFF9AB1FF),
            cursorColor = TaskColabBlue
        )
    )
}

private fun ProjectStatus.label(): String = when (this) {
    ProjectStatus.ACTIVE -> "Activo"
    ProjectStatus.PAUSED -> "Pausado"
    ProjectStatus.ARCHIVED -> "Archivado"
}

private fun String.toColor(): Color =
    runCatching { Color(android.graphics.Color.parseColor(this)) }.getOrDefault(TaskColabBlue)
