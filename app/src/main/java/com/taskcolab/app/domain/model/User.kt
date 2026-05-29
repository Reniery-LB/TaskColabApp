package com.taskcolab.app.domain.model

data class User(
    val id: Int,
    val fullName: String,
    val email: String,
    val role: UserRole,
    val isActive: Boolean,
    val avatarUrl: String? = null
)
