package com.taskcolab.app.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskcolab.app.core.designsystem.component.TaskColabGradientBackground
import com.taskcolab.app.core.designsystem.component.TaskColabPrimaryButton
import com.taskcolab.app.core.designsystem.theme.TaskColabBlue
import com.taskcolab.app.core.designsystem.theme.TaskColabInk
import com.taskcolab.app.core.designsystem.theme.TaskColabLine
import com.taskcolab.app.core.designsystem.theme.TaskColabMuted
import com.taskcolab.app.core.designsystem.theme.TaskColabWhite

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf(FieldState()) }
    var fullName by remember { mutableStateOf(FieldState()) }
    var password by remember { mutableStateOf(FieldState()) }
    var confirmation by remember { mutableStateOf(FieldState()) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmationVisible by remember { mutableStateOf(false) }
    var registerAsAdmin by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    fun submit() {
        val emailError = validateEmail(email.value)
        val nameError = validateFullName(fullName.value)
        val passwordError = validatePassword(password.value)
        val confirmationError = validatePasswordConfirmation(password.value, confirmation.value)

        email = email.copy(value = email.value.trim(), error = emailError)
        fullName = fullName.copy(value = fullName.value.trim().replace(Regex("\\s+"), " "), error = nameError)
        password = password.copy(error = passwordError)
        confirmation = confirmation.copy(error = confirmationError)

        if (
            emailError == null &&
            nameError == null &&
            passwordError == null &&
            confirmationError == null
        ) {
            focusManager.clearFocus()
            onRegisterSuccess()
        }
    }

    TaskColabGradientBackground(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 58.dp, bottom = 26.dp)
            ) {
                Text(
                    text = "Crear Cuenta",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TaskColabWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    lineHeight = 44.sp
                )

                Text(
                    text = "Únete a la mejor plataforma de gestión.",
                    style = MaterialTheme.typography.titleMedium,
                    color = TaskColabWhite,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                AuthCard(
                    modifier = Modifier.widthIn(max = 420.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 24.dp,
                        vertical = 10.dp
                    )
                ) {
                    AuthTextField(
                        label = "Correo electrónico",
                        value = email.value,
                        onValueChange = { email = FieldState(it.take(120), null) },
                        placeholder = "ejemplo@correo.com",
                        error = email.error,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(17.dp))

                    AuthTextField(
                        label = "Nombre completo",
                        value = fullName.value,
                        onValueChange = { fullName = FieldState(it.take(80), null) },
                        placeholder = "Rodrigo Castañeda...",
                        error = fullName.error,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(17.dp))

                    AuthTextField(
                        label = "Contraseña",
                        value = password.value,
                        onValueChange = { password = FieldState(it.take(64), null) },
                        placeholder = "Mínimo 8 caracteres",
                        error = password.error,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) {
                                        Icons.Filled.VisibilityOff
                                    } else {
                                        Icons.Filled.Visibility
                                    },
                                    contentDescription = if (passwordVisible) {
                                        "Ocultar contraseña"
                                    } else {
                                        "Mostrar contraseña"
                                    },
                                    tint = TaskColabBlue
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(17.dp))

                    AuthTextField(
                        label = "Confirmar contraseña",
                        value = confirmation.value,
                        onValueChange = { confirmation = FieldState(it.take(64), null) },
                        placeholder = "Repetir contraseña...",
                        error = confirmation.error,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { submit() }),
                        visualTransformation = if (confirmationVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(onClick = { confirmationVisible = !confirmationVisible }) {
                                Icon(
                                    imageVector = if (confirmationVisible) {
                                        Icons.Filled.VisibilityOff
                                    } else {
                                        Icons.Filled.Visibility
                                    },
                                    contentDescription = if (confirmationVisible) {
                                        "Ocultar confirmación"
                                    } else {
                                        "Mostrar confirmación"
                                    },
                                    tint = TaskColabBlue
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    AdminOptionCard(
                        checked = registerAsAdmin,
                        onCheckedChange = { registerAsAdmin = it }
                    )

                    Spacer(modifier = Modifier.height(34.dp))

                    TaskColabPrimaryButton(
                        text = "Registrarme",
                        onClick = { submit() }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AuthBottomLink(
                        helperText = "¿Ya eres miembro? ",
                        actionText = "Inicia sesión",
                        onClick = onNavigateToLogin
                    )
                }
            }
        }
    }
}


@Composable
private fun AdminOptionCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = TaskColabBlue.copy(alpha = 0.10f),
                spotColor = TaskColabInk.copy(alpha = 0.20f)
            )
            .background(TaskColabLine.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = TaskColabBlue,
                uncheckedColor = TaskColabInk
            )
        )

        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = "Registrar como Administrador",
                style = MaterialTheme.typography.labelMedium,
                color = TaskColabInk,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Permite gestionar usuarios y usos avanzados.",
                style = MaterialTheme.typography.labelSmall,
                color = TaskColabMuted
            )
        }
    }
}
