package com.taskcolab.app.feature.welcome

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskcolab.app.core.designsystem.component.TaskColabGradientBackground
import com.taskcolab.app.core.designsystem.component.TaskColabLogoMark
import com.taskcolab.app.core.designsystem.component.TaskColabOutlinedButton
import com.taskcolab.app.core.designsystem.component.TaskColabPrimaryButton
import com.taskcolab.app.core.designsystem.theme.InterFontFamily
import com.taskcolab.app.core.designsystem.theme.TaskColabBlack
import com.taskcolab.app.core.designsystem.theme.TaskColabBlue
import com.taskcolab.app.core.designsystem.theme.TaskColabMuted
import com.taskcolab.app.core.designsystem.theme.TaskColabTheme
import com.taskcolab.app.core.designsystem.theme.TaskColabWhite

@Composable
fun WelcomeScreen(
    onCreateAccount: () -> Unit,
    onLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    TaskColabGradientBackground(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TaskColabLogoMark()

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = TaskColabWhite,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Task")
                        }
                        withStyle(
                            SpanStyle(
                                color = TaskColabBlack,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Colab")
                        }
                    },
                    fontFamily = InterFontFamily,
                    fontSize = 60.sp,
                    lineHeight = 62.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Gestiona, colabora y cumple\ntus metas en equipo",
                    style = MaterialTheme.typography.titleMedium,
                    color = TaskColabWhite,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    modifier = Modifier.widthIn(max = 320.dp)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TaskColabOutlinedButton(
                    text = "Comenzar ahora",
                    onClick = onCreateAccount
                )

                Spacer(modifier = Modifier.height(18.dp))

                TaskColabPrimaryButton(
                    text = "Ya tengo cuenta",
                    onClick = onLogin
                )

                Spacer(modifier = Modifier.height(58.dp))

                Text(
                    text = "©2026 - TaskColab",
                    style = MaterialTheme.typography.labelMedium,
                    color = TaskColabMuted,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun WelcomeScreenPreview() {
    TaskColabTheme(darkTheme = false) {
        WelcomeScreen(
            onCreateAccount = {},
            onLogin = {}
        )
    }
}
