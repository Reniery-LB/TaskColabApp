package com.taskcolab.app.data.remote.dto

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class RemoteUser(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName("is_admin")
    val isAdmin: Boolean,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    val notes: String? = null,
    @SerializedName("is_active")
    val isActive: Boolean = true,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("last_login")
    val lastLogin: String? = null,
    @SerializedName("assigned_count")
    val assignedCount: Int? = null
)

data class RemoteAssignedUser(
    val id: Int,
    val name: String
)

data class RemoteTask(
    val id: Int,
    @SerializedName("board_id")
    val boardId: Int,
    @SerializedName("board_title")
    val boardTitle: String? = null,
    val title: String,
    val description: String? = null,
    val status: String,
    val priority: String,
    @SerializedName("due_date")
    val dueDate: String? = null,
    @SerializedName("created_by")
    val createdBy: Int? = null,
    @SerializedName("created_by_name")
    val createdByName: String? = null,
    @SerializedName("assigned_users")
    val assignedUsers: List<RemoteAssignedUser> = emptyList(),
    val position: Int = 0,
    @SerializedName("column_created")
    val columnCreated: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class RemoteBoard(
    val id: Int,
    @SerializedName("project_id")
    val projectId: Int? = null,
    @SerializedName("owner_id")
    val ownerId: Int? = null,
    @SerializedName("owner_name")
    val ownerName: String? = null,
    val title: String,
    val description: String? = null,
    val visibility: String? = null,
    val color: String? = null,
    @SerializedName("total_tasks")
    val totalTasks: Int = 0,
    @SerializedName("pending_tasks")
    val pendingTasks: Int = 0,
    @SerializedName("in_progress_tasks")
    val inProgressTasks: Int = 0,
    @SerializedName("done_tasks")
    val doneTasks: Int = 0,
    @SerializedName("members_count")
    val membersCount: Int = 0,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class RemoteProject(
    val id: Int,
    val name: String,
    val description: String? = null,
    @SerializedName("owner_id")
    val ownerId: Int? = null,
    val status: String = "active",
    val color: String? = null,
    @SerializedName("due_date")
    val dueDate: String? = null,
    @SerializedName("board_id")
    val boardId: Int = 0,
    @SerializedName("total_tasks")
    val totalTasks: Int = 0,
    @SerializedName("pending_tasks")
    val pendingTasks: Int = 0,
    @SerializedName("in_progress_tasks")
    val inProgressTasks: Int = 0,
    @SerializedName("done_tasks")
    val doneTasks: Int = 0,
    @SerializedName("members_count")
    val membersCount: Int = 0,
    val progress: Int = 0,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class RemoteConversation(
    val id: Int,
    val type: String,
    val title: String,
    @SerializedName("project_id")
    val projectId: Int? = null,
    @SerializedName("task_id")
    val taskId: Int? = null,
    @SerializedName("last_message_body")
    val lastMessageBody: String? = null,
    @SerializedName("last_message_id")
    val lastMessageId: Int? = null,
    @SerializedName("unread_count")
    val unreadCount: Int = 0,
    @SerializedName("can_delete")
    val canDelete: Boolean = false,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("last_message_at")
    val lastMessageAt: String? = null
)

data class RemoteMessage(
    val id: Int,
    @SerializedName("conversation_id")
    val conversationId: Int,
    @SerializedName("user_id")
    val userId: Int? = null,
    @SerializedName("user_name")
    val userName: String,
    val body: String,
    @SerializedName("is_mine")
    val isMine: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String? = null
)

data class RemoteReportData(
    @SerializedName("general_stats")
    val generalStats: RemoteGeneralStats = RemoteGeneralStats(),
    @SerializedName("state_distribution")
    val stateDistribution: List<RemoteStateDistribution> = emptyList(),
    @SerializedName("active_users")
    val activeUsers: List<RemoteReportUser> = emptyList(),
    @SerializedName("alert_tasks")
    val alertTasks: List<RemoteAlertTask> = emptyList()
)

data class RemoteGeneralStats(
    @SerializedName("total_tareas")
    val totalTasks: Int = 0,
    val pendiente: Int = 0,
    @SerializedName("en_proceso")
    val inProgress: Int = 0,
    val completado: Int = 0,
    val atrasadas: Int = 0,
    val proximas: Int = 0,
    val productividad: Int = 0,
    @SerializedName("usuarios_activos")
    val activeUsers: Int = 0
)

data class RemoteStateDistribution(
    val status: String,
    @SerializedName("status_display")
    val statusDisplay: String,
    @SerializedName("total_tasks")
    val totalTasks: Int,
    val percentage: Float = 0f
)

data class RemoteReportUser(
    val id: Int,
    val usuario: String,
    val email: String? = null,
    @SerializedName("tareas_asignadas")
    val assignedTasks: Int = 0,
    @SerializedName("tareas_completadas")
    val completedTasks: Int = 0,
    @SerializedName("tareas_atrasadas")
    val overdueTasks: Int = 0
)

data class RemoteAlertTask(
    val id: Int,
    val title: String,
    val status: String,
    val priority: String,
    @SerializedName("due_date")
    val dueDate: String? = null,
    @SerializedName("board_title")
    val boardTitle: String? = null,
    @SerializedName("assigned_users")
    val assignedUsers: String? = null,
    @SerializedName("alert_label")
    val alertLabel: String? = null
)

data class SyncEvent(
    val id: Long,
    @SerializedName("event_type")
    val eventType: String,
    @SerializedName("entity_type")
    val entityType: String,
    @SerializedName("entity_id")
    val entityId: Int? = null,
    @SerializedName("board_id")
    val boardId: Int? = null,
    @SerializedName("user_id")
    val userId: Int? = null,
    val payload: JsonObject? = null,
    @SerializedName("created_at")
    val createdAt: String? = null
)
