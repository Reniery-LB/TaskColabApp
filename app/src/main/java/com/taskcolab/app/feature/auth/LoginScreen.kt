package com.taskcolab.app.feature.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskcolab.app.core.designsystem.component.TaskColabGradientBackground
import com.taskcolab.app.core.designsystem.component.TaskColabPrimaryButton
import com.taskcolab.app.core.designsystem.theme.TaskColabBlue
import com.taskcolab.app.core.designsystem.theme.TaskColabWhite

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf(FieldState()) }
    var password by remember { mutableStateOf(FieldState()) }
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    fun submit() {
        val emailError = validateEmail(email.value)
        val passwordError = validateLoginPassword(password.value)
        email = email.copy(value = email.value.trim(), error = emailError)
        password = password.copy(error = passwordError)

        if (emailError == null && passwordError == null) {
            focusManager.clearFocus()
            onLoginSuccess()
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
                    .padding(top = 58.dp),
            ) {
                Text(
                    text = "¡Hola de nuevo!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TaskColabWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 39.sp,
                    lineHeight = 44.sp
                )

                Text(
                    text = "Ingresa tus credenciales para continuar.",
                    style = MaterialTheme.typography.titleMedium,
                    color = TaskColabWhite,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                AuthCard(
                    modifier = Modifier.widthIn(max = 420.dp)
                ) {
                    AuthTextField(
                        label = "Correo electrónico",
                        value = email.value,
                        onValueChange = {
                            email = FieldState(
                                value = it.take(120),
                                error = null
                            )
                        },
                        placeholder = "ejemplo@correo.com",
                        error = email.error,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AuthTextField(
                        label = "Contraseña",
                        value = password.value,
                        onValueChange = {
                            password = FieldState(
                                value = it.take(64),
                                error = null
                            )
                        },
                        placeholder = "Mínimo 8 caracteres",
                        error = password.error,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { submit() }),
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

                    Spacer(modifier = Modifier.height(14.dp))

                    AuthTextAction(
                        text = "¿Olvidaste tu contraseña?",
                        onClick = onForgotPassword,
                        modifier = Modifier.align(Alignment.End)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    TaskColabPrimaryButton(
                        text = "Iniciar Sesión",
                        onClick = { submit() }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    AuthBottomLink(
                        helperText = "¿No tienes cuenta? ",
                        actionText = "Registrate",
                        onClick = onNavigateToRegister
                    )
                }
            }
        }
    }
}
