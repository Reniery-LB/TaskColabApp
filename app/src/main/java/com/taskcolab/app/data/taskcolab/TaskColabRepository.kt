package com.taskcolab.app.data.taskcolab

import com.taskcolab.app.data.remote.TaskColabApi
import com.taskcolab.app.data.remote.dto.CreateConversationRequest
import com.taskcolab.app.data.remote.dto.CreateProjectRequest
import com.taskcolab.app.data.remote.dto.CreateTaskRequest
import com.taskcolab.app.data.remote.dto.DeleteConversationRequest
import com.taskcolab.app.data.remote.dto.DeleteTasksRequest
import com.taskcolab.app.data.remote.dto.RemoteAssignedUser
import com.taskcolab.app.data.remote.dto.RemoteAlertTask
import com.taskcolab.app.data.remote.dto.RemoteConversation
import com.taskcolab.app.data.remote.dto.RemoteMessage
import com.taskcolab.app.data.remote.dto.RemoteProject
import com.taskcolab.app.data.remote.dto.RemoteReportData
import com.taskcolab.app.data.remote.dto.RemoteTask
import com.taskcolab.app.data.remote.dto.RemoteUser
import com.taskcolab.app.data.remote.dto.SendMessageRequest
import com.taskcolab.app.data.remote.dto.UpdateProjectRequest
import com.taskcolab.app.data.remote.dto.UpdateTaskRequest
import com.taskcolab.app.domain.model.ChatMessage
import com.taskcolab.app.domain.model.Conversation
import com.taskcolab.app.domain.model.Project
import com.taskcolab.app.domain.model.ProjectStatus
import com.taskcolab.app.domain.model.ReportAlert
import com.taskcolab.app.domain.model.ReportDashboard
import com.taskcolab.app.domain.model.ReportState
import com.taskcolab.app.domain.model.TaskCard
import com.taskcolab.app.domain.model.TaskPriority
import com.taskcolab.app.domain.model.TaskStatus
import com.taskcolab.app.domain.model.User
import com.taskcolab.app.domain.model.UserRole
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class TaskColabRepository @Inject constructor(
    private val api: TaskColabApi
) {
    private val _activeProject = MutableStateFlow<Project?>(null)
    val activeProject: StateFlow<Project?> = _activeProject.asStateFlow()

    fun selectProject(project: Project) {
        _activeProject.value = project
    }

    suspend fun getProjects(): Result<List<Project>> =
        runCatching {
            val response = api.getProjects()
            if (!response.ok) error(response.message ?: "No se pudieron cargar proyectos")
            response.projects.map { it.toProject() }.also { projects ->
                if (_activeProject.value == null && projects.isNotEmpty()) {
                    _activeProject.value = projects.first()
                }
            }
        }

    suspend fun createProject(
        name: String,
        description: String,
        color: String,
        dueDate: String?
    ): Result<Project> =
        runCatching {
            val response = api.createProject(CreateProjectRequest(name, description, color, dueDate))
            if (!response.ok || response.project == null) {
                error(response.message ?: "No se pudo crear el proyecto")
            }
            response.project.toProject().also { _activeProject.value = it }
        }

    suspend fun pauseProject(project: Project): Result<Project> =
        updateProjectStatus(project, if (project.status == ProjectStatus.PAUSED) "active" else "paused")

    suspend fun archiveProject(project: Project): Result<Unit> =
        runCatching {
            val response = api.archiveProject(UpdateProjectRequest(projectId = project.id))
            if (!response.ok) error(response.message ?: "No se pudo archivar el proyecto")
            if (_activeProject.value?.id == project.id) _activeProject.value = null
        }

    private suspend fun updateProjectStatus(project: Project, status: String): Result<Project> =
        runCatching {
            val response = api.updateProject(UpdateProjectRequest(projectId = project.id, status = status))
            if (!response.ok || response.project == null) {
                error(response.message ?: "No se pudo actualizar el proyecto")
            }
            response.project.toProject().also { _activeProject.value = it }
        }

    suspend fun getUsers(): Result<List<UserListItemData>> =
        runCatching {
            val response = api.getUsers()
            if (!response.ok) error(response.message ?: "No se pudieron cargar usuarios")
            response.users.map { it.toUserListItemData() }
        }

    suspend fun getBoardTasks(projectId: Int? = activeProject.value?.id): Result<List<TaskCard>> =
        runCatching {
            val response = api.getTasks(projectId = projectId)
            if (!response.ok) error(response.message ?: "No se pudieron cargar tareas")
            response.tasks.map { it.toTaskCard() }
        }

    suspend fun createTask(
        title: String,
        description: String,
        status: TaskStatus,
        priority: TaskPriority,
        dueDate: String,
        assignedUsers: List<User>,
        project: Project? = activeProject.value
    ): Result<Unit> =
        runCatching {
            val response = api.createTask(
                CreateTaskRequest(
                    title = title,
                    description = description,
                    status = status.toApiStatus(),
                    priority = priority.toApiPriority(),
                    dueDate = dueDate.toApiDateOrNull(),
                    boardId = project?.boardId?.takeIf { it > 0 },
                    projectId = project?.id,
                    assignedTo = assignedUsers.map { it.id }
                )
            )
            if (!response.ok) error(response.message ?: "No se pudo crear la tarea")
        }

    suspend fun moveTask(taskId: Int, status: TaskStatus): Result<Unit> =
        runCatching {
            val response = api.updateTask(UpdateTaskRequest(taskId = taskId, status = status.toApiStatus()))
            if (!response.ok) error(response.message ?: "No se pudo mover la tarea")
        }

    suspend fun deleteTasks(taskIds: List<Int>): Result<Unit> =
        runCatching {
            val response = api.deleteTasks(DeleteTasksRequest(taskIds))
            if (!response.ok) error(response.message ?: "No se pudieron eliminar las tareas")
        }

    suspend fun getReportDashboard(): Result<ReportDashboard> =
        runCatching {
            val response = api.getReportDashboard()
            if (!response.ok || response.data == null) {
                error(response.message ?: "No se pudieron cargar reportes")
            }
            response.data.toReportDashboard()
        }

    suspend fun getConversations(): Result<List<Conversation>> =
        runCatching {
            val response = api.getConversations()
            if (!response.ok) error(response.message ?: "No se pudieron cargar conversaciones")
            response.conversations.map { it.toConversation() }
        }

    suspend fun ensureProjectConversation(projectId: Int): Result<Conversation> =
        runCatching {
            val response = api.createConversation(CreateConversationRequest(type = "project", projectId = projectId))
            if (!response.ok || response.conversation == null) {
                error(response.message ?: "No se pudo abrir el chat del proyecto")
            }
            response.conversation.toConversation()
        }

    suspend fun createDirectConversation(userId: Int): Result<Conversation> =
        runCatching {
            val response = api.createConversation(CreateConversationRequest(type = "direct", userId = userId))
            if (!response.ok || response.conversation == null) {
                error(response.message ?: "No se pudo crear el chat privado")
            }
            response.conversation.toConversation()
        }

    suspend fun getMessages(conversationId: Int, afterId: Int = 0): Result<List<ChatMessage>> =
        runCatching {
            val response = api.getMessages(conversationId = conversationId, afterId = afterId)
            if (!response.ok) error(response.message ?: "No se pudieron cargar mensajes")
            response.messages.map { it.toChatMessage() }
        }

    suspend fun sendMessage(conversationId: Int, body: String): Result<ChatMessage> =
        runCatching {
            val response = api.sendMessage(SendMessageRequest(conversationId, body))
            if (!response.ok || response.data == null) {
                error(response.message ?: "No se pudo enviar el mensaje")
            }
            response.data.toChatMessage()
        }

    suspend fun deleteConversation(conversationId: Int): Result<Unit> =
        runCatching {
            val response = api.deleteConversation(DeleteConversationRequest(conversationId))
            if (!response.ok) error(response.message ?: "No se pudo eliminar el chat")
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

private fun RemoteProject.toProject(): Project =
    Project(
        id = id,
        name = name,
        description = description.orEmpty(),
        status = when (status.lowercase()) {
            "paused" -> ProjectStatus.PAUSED
            "archived" -> ProjectStatus.ARCHIVED
            else -> ProjectStatus.ACTIVE
        },
        color = color ?: "#1B5CFF",
        dueDate = dueDate.toShortDisplayDate(),
        boardId = boardId,
        totalTasks = totalTasks,
        pendingTasks = pendingTasks,
        inProgressTasks = inProgressTasks,
        doneTasks = doneTasks,
        membersCount = membersCount,
        progress = progress
    )

private fun RemoteConversation.toConversation(): Conversation =
    Conversation(
        id = id,
        type = type,
        title = title,
        projectId = projectId,
        lastMessage = lastMessageBody.orEmpty().ifBlank { "Sin mensajes todavía" },
        unreadCount = unreadCount,
        canDelete = canDelete
    )

private fun RemoteMessage.toChatMessage(): ChatMessage =
    ChatMessage(
        id = id,
        conversationId = conversationId,
        userName = userName,
        body = body,
        isMine = isMine,
        createdAt = createdAt.orEmpty()
    )

private fun RemoteReportData.toReportDashboard(): ReportDashboard =
    ReportDashboard(
        totalTasks = generalStats.totalTasks,
        pending = generalStats.pendiente,
        inProgress = generalStats.inProgress,
        completed = generalStats.completado,
        overdue = generalStats.atrasadas,
        dueSoon = generalStats.proximas,
        productivity = generalStats.productividad,
        activeUsers = generalStats.activeUsers,
        stateDistribution = stateDistribution.map {
            ReportState(
                label = it.statusDisplay,
                totalTasks = it.totalTasks,
                percentage = it.percentage
            )
        },
        alerts = alertTasks.map { it.toReportAlert() }
    )

private fun RemoteAlertTask.toReportAlert(): ReportAlert =
    ReportAlert(
        id = id,
        title = title,
        boardTitle = boardTitle.orEmpty(),
        dueDate = dueDate.toShortDisplayDate(),
        alertLabel = alertLabel.orEmpty(),
        assignedUsers = assignedUsers.orEmpty()
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

private fun TaskStatus.toApiStatus(): String =
    when (this) {
        TaskStatus.PENDING -> "pending"
        TaskStatus.IN_PROGRESS -> "in_progress"
        TaskStatus.COMPLETED -> "done"
    }

private fun TaskPriority.toApiPriority(): String =
    when (this) {
        TaskPriority.HIGH -> "high"
        TaskPriority.MEDIUM -> "medium"
        TaskPriority.LOW -> "low"
    }

private fun String.toApiDateOrNull(): String? {
    if (isBlank() || equals("Sin fecha", ignoreCase = true)) return null

    return runCatching {
        LocalDate.parse(this, DateTimeFormatter.ofPattern("dd-MM-yy"))
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
    }.getOrElse {
        runCatching {
            LocalDate.parse(this, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .format(DateTimeFormatter.ISO_LOCAL_DATE)
        }.getOrNull()
    }
}

private fun String?.toShortDisplayDate(): String {
    if (isNullOrBlank()) return "Sin fecha"

    return runCatching {
        LocalDate.parse(this).format(DateTimeFormatter.ofPattern("dd-MM-yy"))
    }.getOrDefault(this)
}
