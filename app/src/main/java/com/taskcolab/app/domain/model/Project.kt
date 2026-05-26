package com.taskcolab.app.domain.model

data class Project(
    val id: Int,
    val name: String,
    val description: String,
    val status: ProjectStatus,
    val color: String,
    val dueDate: String,
    val boardId: Int,
    val totalTasks: Int,
    val pendingTasks: Int,
    val inProgressTasks: Int,
    val doneTasks: Int,
    val membersCount: Int,
    val progress: Int
)

enum class ProjectStatus {
    ACTIVE,
    PAUSED,
    ARCHIVED
}
