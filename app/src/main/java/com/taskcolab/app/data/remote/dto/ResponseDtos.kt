package com.taskcolab.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TaskListResponse(
    val ok: Boolean,
    val tasks: List<RemoteTask> = emptyList(),
    val count: Int = 0,
    val message: String? = null
)

data class BoardListResponse(
    val ok: Boolean,
    val boards: List<RemoteBoard> = emptyList(),
    val count: Int = 0,
    val message: String? = null
)

data class ProjectListResponse(
    val ok: Boolean,
    val projects: List<RemoteProject> = emptyList(),
    val count: Int = 0,
    val message: String? = null
)

data class ProjectResponse(
    val ok: Boolean,
    val project: RemoteProject? = null,
    val message: String? = null
)

data class UserListResponse(
    val ok: Boolean,
    val users: List<RemoteUser> = emptyList(),
    val count: Int = 0,
    val message: String? = null
)

data class BasicApiResponse(
    val ok: Boolean,
    val message: String? = null
)

data class TaskMutationResponse(
    val ok: Boolean,
    @SerializedName("task_id")
    val taskId: Int? = null,
    @SerializedName("task_ids")
    val taskIds: List<Int> = emptyList(),
    val message: String? = null
)

data class ConversationListResponse(
    val ok: Boolean,
    val conversations: List<RemoteConversation> = emptyList(),
    val count: Int = 0,
    val message: String? = null
)

data class ConversationResponse(
    val ok: Boolean,
    val conversation: RemoteConversation? = null,
    val message: String? = null
)

data class MessageListResponse(
    val ok: Boolean,
    val messages: List<RemoteMessage> = emptyList(),
    val count: Int = 0,
    val message: String? = null
)

data class SendMessageResponse(
    val ok: Boolean,
    val data: RemoteMessage? = null,
    val message: String? = null
)

data class ReportDashboardResponse(
    val ok: Boolean,
    val data: RemoteReportData? = null,
    val message: String? = null
)

data class SyncChangesResponse(
    val ok: Boolean,
    val events: List<SyncEvent> = emptyList(),
    val count: Int = 0,
    @SerializedName("latest_id")
    val latestId: Long = 0,
    val message: String? = null
)
