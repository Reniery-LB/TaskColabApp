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

data class UserListResponse(
    val ok: Boolean,
    val users: List<RemoteUser> = emptyList(),
    val count: Int = 0,
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
