package com.taskcolab.app.domain.model

data class Conversation(
    val id: Int,
    val type: String,
    val title: String,
    val projectId: Int?,
    val lastMessage: String,
    val unreadCount: Int,
    val canDelete: Boolean
)

data class ChatMessage(
    val id: Int,
    val conversationId: Int,
    val userName: String,
    val body: String,
    val isMine: Boolean,
    val createdAt: String
)
