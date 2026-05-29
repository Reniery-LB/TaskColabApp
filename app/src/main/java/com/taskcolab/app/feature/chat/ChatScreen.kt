package com.taskcolab.app.feature.chat

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskcolab.app.core.designsystem.theme.TaskColabBlue
import com.taskcolab.app.core.designsystem.theme.TaskColabInk
import com.taskcolab.app.core.designsystem.theme.TaskColabMuted
import com.taskcolab.app.core.designsystem.theme.TaskColabWhite
import com.taskcolab.app.domain.model.ChatMessage
import com.taskcolab.app.domain.model.Conversation

@Composable
fun ChatScreen(
    onBackToProjects: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var draft by remember { mutableStateOf("") }
    var conversationToDelete by remember { mutableStateOf<Conversation?>(null) }

    Scaffold(
        containerColor = TaskColabWhite,
        topBar = {
            ChatHeader(
                title = uiState.selectedConversation?.title ?: uiState.activeProject?.name ?: "Chat",
                onBack = onBackToProjects
            )
        },
        bottomBar = {
            if (uiState.selectedConversation != null) {
                MessageComposer(
                    value = draft,
                    enabled = true,
                    onValueChange = { draft = it.take(1000) },
                    onSend = {
                        if (draft.isNotBlank()) {
                            viewModel.sendMessage(draft.trim())
                            draft = ""
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        val selectedConversation = uiState.selectedConversation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedConversation == null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF4F6FF))
                        .padding(bottom = 76.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.activeProject
                        ?.takeIf { project -> uiState.conversations.none { it.projectId == project.id } }
                        ?.let { project ->
                        item(key = "project-${project.id}") {
                            ProjectConversationShortcut(
                                projectName = project.name,
                                onClick = { viewModel.openProjectChat(project.id) }
                            )
                        }
                    }

                    if (uiState.conversations.isNotEmpty()) {
                        item(key = "conversations-title") {
                            ConversationSectionLabel("Conversaciones")
                        }
                    }
                    items(uiState.conversations, key = { it.id }) { conversation ->
                        ConversationRow(
                            conversation = conversation,
                            selected = false,
                            onClick = { viewModel.selectConversation(conversation) },
                            onDelete = { conversationToDelete = conversation }
                        )
                    }

                    if (uiState.directUsers.isNotEmpty()) {
                        item(key = "direct-title") {
                            ConversationSectionLabel("Chats privados")
                        }
                    }
                    items(uiState.directUsers, key = { "direct-${it.id}" }) { user ->
                        DirectUserRow(
                            fullName = user.fullName,
                            email = user.email,
                            onClick = { viewModel.openDirectChat(user.id) }
                        )
                    }
                }
                EmptyChatSelection(
                    isLoading = uiState.isLoading,
                    error = uiState.error,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    ActiveConversationHeader(
                        conversation = selectedConversation,
                        onClose = { viewModel.closeConversation() },
                        onDelete = { conversationToDelete = selectedConversation }
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (uiState.error != null) {
                            item { Text(uiState.error.orEmpty(), color = Color(0xFFD71920)) }
                        }
                        if (uiState.messages.isEmpty()) {
                            item {
                                Text(
                                    text = if (uiState.isLoading) "Cargando mensajes..." else "Aún no hay mensajes",
                                    color = TaskColabMuted,
                                    modifier = Modifier.padding(top = 40.dp)
                                )
                            }
                        }
                        items(uiState.messages, key = { it.id }) { message ->
                            MessageBubble(message)
                        }
                    }
                }
            }
        }
    }

    conversationToDelete?.let { conversation ->
        AlertDialog(
            onDismissRequest = { conversationToDelete = null },
            containerColor = TaskColabWhite,
            title = {
                Text(
                    text = if (conversation.type == "project") "Borrar chat" else "Eliminar chat",
                    color = TaskColabInk,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (conversation.type == "project") {
                        "El chat dejará de estar disponible para el equipo."
                    } else {
                        "La conversación se eliminará de tus chats."
                    },
                    color = TaskColabMuted
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteConversation(conversation)
                        conversationToDelete = null
                    }
                ) {
                    Text("Eliminar", color = Color(0xFFD71920), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { conversationToDelete = null }) {
                    Text("Cancelar", color = TaskColabBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun ChatHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TaskColabBlue)
            .padding(start = 6.dp, top = 42.dp, end = 18.dp, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = TaskColabWhite)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Chat",
                style = MaterialTheme.typography.headlineSmall,
                color = TaskColabWhite,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = TaskColabWhite.copy(alpha = 0.9f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ConversationSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = TaskColabMuted,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 2.dp)
    )
}

@Composable
private fun ProjectConversationShortcut(
    projectName: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = TaskColabWhite
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
            Text(
                text = "General - $projectName",
                color = TaskColabInk,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Abrir chat del proyecto",
                color = TaskColabMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ConversationRow(
    conversation: Conversation,
    selected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) TaskColabBlue else TaskColabWhite
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = conversation.title,
                    color = if (selected) TaskColabWhite else TaskColabInk,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = conversation.lastMessage,
                    color = if (selected) TaskColabWhite.copy(alpha = 0.78f) else TaskColabMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (conversation.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFFD71920), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(conversation.unreadCount.toString(), color = TaskColabWhite, fontWeight = FontWeight.Bold)
                }
            }
            if (conversation.canDelete) {
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar chat",
                        tint = if (selected) TaskColabWhite else Color(0xFFD71920),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DirectUserRow(
    fullName: String,
    email: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = TaskColabWhite
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(Color(0xFFE9F0FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = TaskColabBlue,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fullName,
                    color = TaskColabInk,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = email.ifBlank { "Chat privado" },
                    color = TaskColabMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ActiveConversationHeader(
    conversation: Conversation,
    onClose: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TaskColabWhite)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = conversation.title,
                style = MaterialTheme.typography.titleMedium,
                color = TaskColabInk,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (conversation.type == "project") "Proyecto" else "Conversación",
                style = MaterialTheme.typography.labelMedium,
                color = TaskColabMuted
            )
        }
        if (conversation.canDelete) {
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar chat", tint = Color(0xFFD71920))
            }
        }
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Cerrar chat", tint = TaskColabMuted)
        }
    }
}

@Composable
private fun EmptyChatSelection(
    isLoading: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TaskColabWhite)
            .padding(horizontal = 24.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when {
                error != null -> error
                isLoading -> "Cargando conversaciones..."
                else -> "Selecciona una conversación"
            },
            style = MaterialTheme.typography.titleMedium,
            color = if (error != null) Color(0xFFD71920) else TaskColabMuted,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.68f)
                .background(
                    color = if (message.isMine) TaskColabBlue else Color(0xFFEFF2F8),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Text(
                text = message.userName,
                style = MaterialTheme.typography.labelMedium,
                color = if (message.isMine) TaskColabWhite.copy(alpha = 0.82f) else TaskColabMuted,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message.body,
                style = MaterialTheme.typography.bodyMedium,
                color = if (message.isMine) TaskColabWhite else TaskColabInk
            )
        }
    }
}

@Composable
private fun MessageComposer(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TaskColabWhite)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            placeholder = { Text("Escribe un mensaje") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TaskColabBlue,
                unfocusedBorderColor = Color(0xFF9AB1FF),
                cursorColor = TaskColabBlue,
                focusedContainerColor = TaskColabWhite,
                unfocusedContainerColor = TaskColabWhite,
                disabledContainerColor = TaskColabWhite,
                focusedTextColor = TaskColabInk,
                unfocusedTextColor = TaskColabInk
            )
        )
        IconButton(
            onClick = onSend,
            enabled = enabled && value.isNotBlank(),
            modifier = Modifier
                .padding(start = 8.dp)
                .background(TaskColabBlue, CircleShape)
        ) {
            Icon(Icons.Filled.Send, contentDescription = "Enviar", tint = TaskColabWhite)
        }
    }
}
