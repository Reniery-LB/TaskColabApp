package com.taskcolab.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.taskcolab.app.feature.auth.LoginScreen
import com.taskcolab.app.feature.auth.RegisterPlaceholderScreen
import com.taskcolab.app.feature.boards.BoardsPlaceholderScreen
import com.taskcolab.app.feature.profile.ProfilePlaceholderScreen
import com.taskcolab.app.feature.reports.ReportsPlaceholderScreen
import com.taskcolab.app.feature.tasks.TasksPlaceholderScreen
import com.taskcolab.app.feature.users.UsersPlaceholderScreen
import com.taskcolab.app.feature.welcome.WelcomeScreen

@Composable
fun TaskColabNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.WELCOME,
        modifier = modifier
    ) {
        composable(NavRoutes.WELCOME) {
            WelcomeScreen(
                onCreateAccount = { navController.navigate(NavRoutes.REGISTER) },
                onLogin = { navController.navigate(NavRoutes.LOGIN) }
            )
        }
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate(NavRoutes.BOARDS) {
                        popUpTo(NavRoutes.WELCOME) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.REGISTER) {
            RegisterPlaceholderScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.BOARDS) { BoardsPlaceholderScreen() }
        composable(NavRoutes.TASKS) { TasksPlaceholderScreen() }
        composable(NavRoutes.REPORTS) { ReportsPlaceholderScreen() }
        composable(NavRoutes.USERS) { UsersPlaceholderScreen() }
        composable(NavRoutes.PROFILE) { ProfilePlaceholderScreen() }
    }
}
