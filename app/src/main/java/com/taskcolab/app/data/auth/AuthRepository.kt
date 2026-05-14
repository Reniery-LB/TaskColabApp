package com.taskcolab.app.data.auth

import com.google.gson.JsonParser
import com.taskcolab.app.data.remote.TaskColabApi
import com.taskcolab.app.data.remote.dto.LoginRequest
import com.taskcolab.app.data.remote.dto.RegisterRequest
import com.taskcolab.app.data.session.SessionManager
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: TaskColabApi,
    private val sessionManager: SessionManager
) {
    suspend fun login(email: String, password: String): Result<Unit> =
        runApiCatching {
            val response = api.login(LoginRequest(email = email, password = password))
            val token = response.accessToken
            val user = response.user

            if (!response.ok || token.isNullOrBlank() || user == null) {
                error(response.message ?: "No se pudo iniciar sesión")
            }

            sessionManager.saveSession(
                token = token,
                userId = user.id,
                isAdmin = user.isAdmin,
                userName = user.name,
                userEmail = user.email
            )
        }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        isAdmin: Boolean
    ): Result<Unit> =
        runApiCatching {
            val response = api.register(
                RegisterRequest(
                    name = name,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword,
                    isAdmin = isAdmin
                )
            )
            val token = response.accessToken
            val user = response.user

            if (!response.ok || token.isNullOrBlank() || user == null) {
                error(response.message ?: response.errors?.joinToString().orEmpty().ifBlank {
                    "No se pudo crear la cuenta"
                })
            }

            sessionManager.saveSession(
                token = token,
                userId = user.id,
                isAdmin = user.isAdmin,
                userName = user.name,
                userEmail = user.email
            )
        }

    suspend fun logout(): Result<Unit> =
        runApiCatching {
            api.logout()
            sessionManager.clearSession()
        }

    private suspend fun runApiCatching(block: suspend () -> Unit): Result<Unit> {
        return try {
            block()
            Result.success(Unit)
        } catch (throwable: Throwable) {
            Result.failure(IllegalStateException(throwable.toUserMessage()))
        }
    }

    private fun Throwable.toUserMessage(): String {
        if (this is HttpException) {
            val body = response()?.errorBody()?.string()
            val message = body
                ?.let {
                    runCatching {
                        JsonParser.parseString(it).asJsonObject["message"]?.asString
                    }.getOrNull()
                }
                ?.takeIf { it.isNotBlank() }

            return message ?: when (code()) {
                401 -> "Correo o contraseña incorrectos"
                422 -> "Revisa los datos ingresados"
                else -> "Error del servidor (${code()})"
            }
        }

        return message ?: "No se pudo conectar con TaskColab"
    }
}
