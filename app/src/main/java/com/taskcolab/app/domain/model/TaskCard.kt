package com.taskcolab.app.domain.model

data class TaskCard(
    val id: Int,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val dueDate: String,
    val assignedUser: User? = null,
    val assignedUsers: List<User> = assignedUser?.let { listOf(it) } ?: emptyList()
)
