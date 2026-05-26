package com.taskcolab.app.domain.model

data class ReportDashboard(
    val totalTasks: Int,
    val pending: Int,
    val inProgress: Int,
    val completed: Int,
    val overdue: Int,
    val dueSoon: Int,
    val productivity: Int,
    val activeUsers: Int,
    val stateDistribution: List<ReportState>,
    val alerts: List<ReportAlert>
)

data class ReportState(
    val label: String,
    val totalTasks: Int,
    val percentage: Float
)

data class ReportAlert(
    val id: Int,
    val title: String,
    val boardTitle: String,
    val dueDate: String,
    val alertLabel: String,
    val assignedUsers: String
)
