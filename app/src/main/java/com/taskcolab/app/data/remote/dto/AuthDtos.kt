package com.taskcolab.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String,
    @SerializedName("device_name")
    val deviceName: String = "Android"
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("confirm_password")
    val confirmPassword: String,
    @SerializedName("is_admin")
    val isAdmin: Boolean,
    @SerializedName("device_name")
    val deviceName: String = "Android"
)

data class AuthResponse(
    val ok: Boolean,
    val message: String? = null,
    @SerializedName("token_type")
    val tokenType: String? = null,
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("expires_at")
    val expiresAt: String? = null,
    val user: RemoteUser? = null,
    val errors: List<String>? = null
)
