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
