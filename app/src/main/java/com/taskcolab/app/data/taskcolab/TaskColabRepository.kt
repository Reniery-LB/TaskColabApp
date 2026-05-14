package com.taskcolab.app.data.taskcolab

import com.taskcolab.app.data.remote.TaskColabApi
import com.taskcolab.app.data.remote.dto.RemoteAssignedUser
import com.taskcolab.app.data.remote.dto.RemoteTask
import com.taskcolab.app.data.remote.dto.RemoteUser
import com.taskcolab.app.domain.model.TaskCard
import com.taskcolab.app.domain.model.TaskPriority
import com.taskcolab.app.domain.model.TaskStatus
import com.taskcolab.app.domain.model.User
import com.taskcolab.app.domain.model.UserRole
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskColabRepository @Inject constructor(
    private val api: TaskColabApi
) {
    suspend fun getUsers(): Result<List<UserListItemData>> =
        runCatching {
            val response = api.getUsers()
            if (!response.ok) error(response.message ?: "No se pudieron cargar usuarios")
            response.users.map { it.toUserListItemData() }
        }

    suspend fun getBoardTasks(): Result<List<TaskCard>> =
        runCatching {
            val response = api.getTasks()
            if (!response.ok) error(response.message ?: "No se pudieron cargar tareas")
            response.tasks.map { it.toTaskCard() }
        }
}

data class UserListItemData(
    val id: Int,
    val fullName: String,
    val email: String,
    val assignedTasks: List<String>,
    val taskCount: Int,
    val notes: String
)

private fun RemoteTask.toTaskCard(): TaskCard =
    TaskCard(
        id = id,
        title = title,
        description = description.orEmpty(),
        status = status.toTaskStatus(),
        priority = priority.toTaskPriority(),
        dueDate = dueDate.toShortDisplayDate(),
        assignedUsers = assignedUsers.map { it.toDomainUser() }
    )

private fun RemoteAssignedUser.toDomainUser(): User =
    User(
        id = id,
        fullName = name,
        email = "",
        role = UserRole.USER,
        isActive = true
    )

private fun RemoteUser.toUserListItemData(): UserListItemData =
    UserListItemData(
        id = id,
        fullName = name,
        email = email,
        assignedTasks = emptyList(),
        taskCount = assignedCount ?: 0,
        notes = notes.orEmpty()
    )

private fun String.toTaskStatus(): TaskStatus =
    when (lowercase()) {
        "in_progress", "in progress", "proceso" -> TaskStatus.IN_PROGRESS
        "done", "completed", "completado" -> TaskStatus.COMPLETED
        else -> TaskStatus.PENDING
    }

private fun String.toTaskPriority(): TaskPriority =
    when (lowercase()) {
        "medium", "media" -> TaskPriority.MEDIUM
        "low", "baja" -> TaskPriority.LOW
        else -> TaskPriority.HIGH
    }

private fun String?.toShortDisplayDate(): String {
    if (isNullOrBlank()) return "Sin fecha"

    return runCatching {
        LocalDate.parse(this).format(DateTimeFormatter.ofPattern("dd-MM-yy"))
    }.getOrDefault(this)
}
