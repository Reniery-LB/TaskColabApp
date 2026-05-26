package com.taskcolab.app.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskcolab.app.data.taskcolab.TaskColabRepository
import com.taskcolab.app.domain.model.ChatMessage
import com.taskcolab.app.domain.model.Conversation
import com.taskcolab.app.domain.model.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val activeProject: Project? = null,
    val conversations: List<Conversation> = emptyList(),
    val selectedConversation: Conversation? = null,
    val messages: List<ChatMessage> = emptyList()
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: TaskColabRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.activeProject.collect { project ->
                _uiState.update { it.copy(activeProject = project) }
                if (project != null) openProjectChat(project.id)
            }
        }
        viewModelScope.launch {
            while (true) {
                refreshConversations(keepSelection = true)
                _uiState.value.selectedConversation?.let { loadMessages(it.id) }
                delay(7_000)
            }
        }
    }

    fun refreshConversations(keepSelection: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getConversations()
                .onSuccess { conversations ->
                    val selected = if (keepSelection) {
                        conversations.firstOrNull { it.id == _uiState.value.selectedConversation?.id }
                            ?: conversations.firstOrNull()
                    } else {
                        conversations.firstOrNull()
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            conversations = conversations,
                            selectedConversation = selected
                        )
                    }
                    selected?.let { loadMessages(it.id) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message) }
                }
        }
    }

    fun openProjectChat(projectId: Int) {
        viewModelScope.launch {
            repository.ensureProjectConversation(projectId)
                .onSuccess { conversation ->
                    _uiState.update { it.copy(selectedConversation = conversation) }
                    refreshConversations(keepSelection = true)
                    loadMessages(conversation.id)
                }
                .onFailure { throwable -> _uiState.update { it.copy(error = throwable.message) } }
        }
    }

    fun selectConversation(conversation: Conversation) {
        _uiState.update { it.copy(selectedConversation = conversation, messages = emptyList()) }
        loadMessages(conversation.id)
    }

    fun loadMessages(conversationId: Int) {
        viewModelScope.launch {
            val afterId = _uiState.value.messages.maxOfOrNull { it.id } ?: 0
            repository.getMessages(conversationId, afterId)
                .onSuccess { incoming ->
                    _uiState.update { state ->
                        val merged = (state.messages + incoming).distinctBy { it.id }.sortedBy { it.id }
                        state.copy(messages = merged)
                    }
                }
                .onFailure { throwable -> _uiState.update { it.copy(error = throwable.message) } }
        }
    }

    fun sendMessage(body: String) {
        val conversation = _uiState.value.selectedConversation ?: return
        viewModelScope.launch {
            repository.sendMessage(conversation.id, body)
                .onSuccess { message ->
                    _uiState.update { it.copy(messages = (it.messages + message).distinctBy { item -> item.id }) }
                    refreshConversations(keepSelection = true)
                }
                .onFailure { throwable -> _uiState.update { it.copy(error = throwable.message) } }
        }
    }
}
