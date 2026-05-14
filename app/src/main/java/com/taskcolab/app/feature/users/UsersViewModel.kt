package com.taskcolab.app.feature.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskcolab.app.data.taskcolab.TaskColabRepository
import com.taskcolab.app.data.taskcolab.UserListItemData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UsersUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val users: List<UserListItemData> = emptyList()
)

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: TaskColabRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    private var localNextId = -1

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getUsers()
                .onSuccess { users ->
                    _uiState.update { it.copy(isLoading = false, users = users) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "No se pudieron cargar usuarios"
                        )
                    }
                }
        }
    }

    fun addLocalUser(form: UserForm) {
        _uiState.update {
            it.copy(
                users = it.users + UserListItemData(
                    id = localNextId--,
                    fullName = form.fullName,
                    email = form.email,
                    assignedTasks = form.tasks,
                    taskCount = form.tasks.size,
                    notes = form.notes
                )
            )
        }
    }

    fun updateLocalUser(userId: Int, form: UserForm) {
        _uiState.update {
            it.copy(
                users = it.users.map { user ->
                    if (user.id == userId) {
                        user.copy(
                            fullName = form.fullName,
                            email = form.email,
                            assignedTasks = form.tasks,
                            taskCount = form.tasks.size,
                            notes = form.notes
                        )
                    } else {
                        user
                    }
                }
            )
        }
    }

    fun deleteLocalUser(userId: Int) {
        _uiState.update { it.copy(users = it.users.filterNot { user -> user.id == userId }) }
    }
}
