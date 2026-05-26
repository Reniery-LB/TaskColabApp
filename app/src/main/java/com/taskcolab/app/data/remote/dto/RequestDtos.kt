package com.taskcolab.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateTaskRequest(
    val title: String,
    val description: String,
    val status: String,
    val priority: String,
    @SerializedName("due_date")
    val dueDate: String?,
    @SerializedName("board_id")
    val boardId: Int? = null,
    @SerializedName("project_id")
    val projectId: Int? = null,
    @SerializedName("assigned_to")
    val assignedTo: List<Int> = emptyList()
)

data class UpdateTaskRequest(
    @SerializedName("task_id")
    val taskId: Int,
    val title: String? = null,
    val description: String? = null,
    val status: String? = null,
    val priority: String? = null,
    @SerializedName("due_date")
    val dueDate: String? = null
)

data class DeleteTasksRequest(
    @SerializedName("task_ids")
    val taskIds: List<Int>
)

data class CreateProjectRequest(
    val name: String,
    val description: String,
    val color: String,
    @SerializedName("due_date")
    val dueDate: String? = null
)

data class UpdateProjectRequest(
    @SerializedName("project_id")
    val projectId: Int,
    val name: String? = null,
    val description: String? = null,
    val color: String? = null,
    @SerializedName("due_date")
    val dueDate: String? = null,
    val status: String? = null
)

data class CreateConversationRequest(
    val type: String,
    @SerializedName("project_id")
    val projectId: Int? = null,
    @SerializedName("user_id")
    val userId: Int? = null
)

data class DeleteConversationRequest(
    @SerializedName("conversation_id")
    val conversationId: Int
)

data class SendMessageRequest(
    @SerializedName("conversation_id")
    val conversationId: Int,
    val body: String
)
