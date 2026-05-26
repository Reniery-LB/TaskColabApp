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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

    Scaffold(
        containerColor = TaskColabWhite,
        topBar = {
            ChatHeader(
                title = uiState.selectedConversation?.title ?: uiState.activeProject?.name ?: "Chat",
                onBack = onBackToProjects
            )
        },
        bottomBar = {
            MessageComposer(
                value = draft,
                enabled = uiState.selectedConversation != null,
                onValueChange = { draft = it.take(1000) },
                onSend = {
                    if (draft.isNotBlank()) {
                        viewModel.sendMessage(draft.trim())
                        draft = ""
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .background(Color(0xFFF4F6FF)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.conversations, key = { it.id }) { conversation ->
                    ConversationRow(
                        conversation = conversation,
                        selected = conversation.id == uiState.selectedConversation?.id,
                        onClick = { viewModel.selectConversation(conversation) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (uiState.error != null) {
                    item { Text(uiState.error.orEmpty(), color = Color(0xFFD71920)) }
                }
                if (uiState.messages.isEmpty()) {
                    item {
                        Text(
                            text = if (uiState.isLoading) "Cargando mensajes..." else "Aún no hay mensajes",
                            color = TaskColabMuted,
                            modifier = Modifier.padding(top = 60.dp)
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
private fun ConversationRow(
    conversation: Conversation,
    selected: Boolean,
    onClick: () -> Unit
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
        }
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
                .fillMaxWidth(0.82f)
                .background(
                    color = if (message.isMine) TaskColabBlue else Color(0xFFEFF2F8),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
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
                style = MaterialTheme.typography.bodyLarge,
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
                cursorColor = TaskColabBlue
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
