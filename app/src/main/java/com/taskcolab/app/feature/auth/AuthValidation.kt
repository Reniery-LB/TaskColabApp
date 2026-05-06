package com.taskcolab.app.feature.auth

private val EmailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

data class FieldState(
    val value: String = "",
    val error: String? = null
)

fun validateEmail(email: String): String? {
    val normalized = email.trim()
    return when {
        normalized.isBlank() -> "Ingresa tu correo electrónico."
        normalized.length > 120 -> "El correo es demasiado largo."
        !EmailPattern.matches(normalized) -> "Ingresa un correo valido."
        else -> null
    }
}

fun validateFullName(name: String): String? {
    val normalized = name.trim()
    return when {
        normalized.isBlank() -> "Ingresa tu nombre completo."
        normalized.length < 3 -> "El nombre debe tener al menos 3 caracteres."
        normalized.length > 80 -> "El nombre es demasiado largo."
        !normalized.contains(" ") -> "Escribe nombre y apellido."
        else -> null
    }
}

fun validatePassword(password: String): String? {
    return when {
        password.isBlank() -> "Ingresa tu contraseña."
        password.length < 8 -> "Usa minimo 8 caracteres."
        password.length > 64 -> "Usa maximo 64 caracteres."
        password.any { it.isWhitespace() } -> "No uses espacios en la contraseña."
        !password.any { it.isUpperCase() } -> "Agrega al menos una mayuscula."
        !password.any { it.isLowerCase() } -> "Agrega al menos una minuscula."
        !password.any { it.isDigit() } -> "Agrega al menos un numero."
        else -> null
    }
}

fun validateLoginPassword(password: String): String? {
    return when {
        password.isBlank() -> "Ingresa tu contraseña."
        password.length < 8 -> "Usa minimo 8 caracteres."
        password.length > 64 -> "Usa maximo 64 caracteres."
        password.any { it.isWhitespace() } -> "No uses espacios en la contraseña."
        else -> null
    }
}

fun validatePasswordConfirmation(password: String, confirmation: String): String? {
    return when {
        confirmation.isBlank() -> "Confirma tu contraseña."
        confirmation != password -> "Las contraseñas no coinciden."
        else -> null
    }
}
