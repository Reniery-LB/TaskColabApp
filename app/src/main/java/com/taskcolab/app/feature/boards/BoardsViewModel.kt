package com.taskcolab.app.feature.boards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskcolab.app.data.taskcolab.TaskColabRepository
import com.taskcolab.app.domain.model.TaskCard
import com.taskcolab.app.domain.model.TaskPriority
import com.taskcolab.app.domain.model.TaskStatus
import com.taskcolab.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BoardsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val cards: List<TaskCard> = emptyList(),
    val users: List<User> = emptyList()
)

@HiltViewModel
class BoardsViewModel @Inject constructor(
    private val repository: TaskColabRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BoardsUiState())
    val uiState: StateFlow<BoardsUiState> = _uiState.asStateFlow()

    private var localNextId = -1

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val tasksResult = repository.getBoardTasks()
            val usersResult = repository.getUsers()

            val error = tasksResult.exceptionOrNull()?.message
                ?: usersResult.exceptionOrNull()?.message

            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = error,
                    cards = tasksResult.getOrDefault(emptyList()),
                    users = usersResult.getOrDefault(emptyList()).map { user ->
                        User(
                            id = user.id,
                            fullName = user.fullName,
                            email = user.email,
                            role = com.taskcolab.app.domain.model.UserRole.USER,
                            isActive = true
                        )
                    }
                )
            }
        }
    }

    fun addLocalCard(
        title: String,
        description: String,
        status: TaskStatus,
        priority: TaskPriority,
        dueDate: String,
        selectedUsers: List<User>
    ) {
        val card = TaskCard(
            id = localNextId--,
            title = title,
            description = description,
            status = status,
            priority = priority,
            dueDate = dueDate,
            assignedUser = selectedUsers.firstOrNull(),
            assignedUsers = selectedUsers
        )
        _uiState.update { it.copy(cards = it.cards + card) }
    }

    fun moveLocalCard(card: TaskCard, status: TaskStatus) {
        _uiState.update {
            it.copy(cards = it.cards.map { current ->
                if (current.id == card.id) current.copy(status = status) else current
            })
        }
    }

    fun deleteLocalCard(card: TaskCard) {
        _uiState.update { it.copy(cards = it.cards.filterNot { current -> current.id == card.id }) }
    }
}
