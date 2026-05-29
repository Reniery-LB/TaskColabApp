package com.taskcolab.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.taskcolab.app.feature.auth.LoginScreen
import com.taskcolab.app.feature.auth.RegisterScreen
import com.taskcolab.app.feature.boards.BoardsPlaceholderScreen
import com.taskcolab.app.feature.chat.ChatScreen
import com.taskcolab.app.feature.profile.ProfilePlaceholderScreen
import com.taskcolab.app.feature.projects.ProjectsScreen
import com.taskcolab.app.feature.reports.ReportsPlaceholderScreen
import com.taskcolab.app.feature.tasks.TasksPlaceholderScreen
import com.taskcolab.app.feature.users.UsersPlaceholderScreen
import com.taskcolab.app.feature.welcome.WelcomeScreen

@Composable
fun TaskColabNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val logoutViewModel: AppLogoutViewModel = hiltViewModel()
    val onLogout: () -> Unit = {
        logoutViewModel.logout {
            navController.navigate(NavRoutes.LOGIN) {
                popUpTo(NavRoutes.WELCOME) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

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
                onLoginSuccess = {
                    navController.navigate(NavRoutes.PROJECTS) {
                        popUpTo(NavRoutes.WELCOME) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(NavRoutes.REGISTER) },
                onForgotPassword = { }
            )
        }
        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(NavRoutes.PROJECTS) {
                        popUpTo(NavRoutes.WELCOME) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.REGISTER) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.PROJECTS) {
            ProjectsScreen(
                onOpenBoard = { navController.navigateSingleTopTo(NavRoutes.BOARDS) },
                onOpenChat = { navController.navigateSingleTopTo(NavRoutes.CHAT) },
                onLogout = onLogout
            )
        }
        composable(NavRoutes.BOARDS) {
            BoardsPlaceholderScreen(
                onNavigateToBoards = { navController.navigateSingleTopTo(NavRoutes.BOARDS) },
                onNavigateToProjects = { navController.navigateSingleTopTo(NavRoutes.PROJECTS) },
                onNavigateToTasks = { navController.navigateSingleTopTo(NavRoutes.TASKS) },
                onNavigateToReports = { navController.navigateSingleTopTo(NavRoutes.REPORTS) },
                onNavigateToUsers = { navController.navigateSingleTopTo(NavRoutes.USERS) },
                onNavigateToProfile = { navController.navigateSingleTopTo(NavRoutes.PROFILE) },
                onOpenChat = { navController.navigateSingleTopTo(NavRoutes.CHAT) },
                onLogout = onLogout
            )
        }
        composable(NavRoutes.TASKS) {
            TasksPlaceholderScreen(
                onNavigateToBoards = { navController.navigateSingleTopTo(NavRoutes.BOARDS) },
                onNavigateToTasks = { navController.navigateSingleTopTo(NavRoutes.TASKS) },
                onNavigateToReports = { navController.navigateSingleTopTo(NavRoutes.REPORTS) },
                onNavigateToUsers = { navController.navigateSingleTopTo(NavRoutes.USERS) },
                onNavigateToProfile = { navController.navigateSingleTopTo(NavRoutes.PROFILE) },
                onOpenChat = { navController.navigateSingleTopTo(NavRoutes.CHAT) },
                onLogout = onLogout
            )
        }
        composable(NavRoutes.REPORTS) {
            ReportsPlaceholderScreen(
                onNavigateToBoards = { navController.navigateSingleTopTo(NavRoutes.BOARDS) },
                onNavigateToTasks = { navController.navigateSingleTopTo(NavRoutes.TASKS) },
                onNavigateToReports = { navController.navigateSingleTopTo(NavRoutes.REPORTS) },
                onNavigateToUsers = { navController.navigateSingleTopTo(NavRoutes.USERS) },
                onNavigateToProfile = { navController.navigateSingleTopTo(NavRoutes.PROFILE) },
                onOpenChat = { navController.navigateSingleTopTo(NavRoutes.CHAT) },
                onLogout = onLogout
            )
        }
        composable(NavRoutes.USERS) {
            UsersPlaceholderScreen(
                onNavigateToBoards = { navController.navigateSingleTopTo(NavRoutes.BOARDS) },
                onNavigateToTasks = { navController.navigateSingleTopTo(NavRoutes.TASKS) },
                onNavigateToReports = { navController.navigateSingleTopTo(NavRoutes.REPORTS) },
                onNavigateToUsers = { navController.navigateSingleTopTo(NavRoutes.USERS) },
                onNavigateToProfile = { navController.navigateSingleTopTo(NavRoutes.PROFILE) },
                onOpenChat = { navController.navigateSingleTopTo(NavRoutes.CHAT) },
                onLogout = onLogout
            )
        }
        composable(NavRoutes.PROFILE) {
            ProfilePlaceholderScreen(
                onNavigateToBoards = { navController.navigateSingleTopTo(NavRoutes.BOARDS) },
                onNavigateToTasks = { navController.navigateSingleTopTo(NavRoutes.TASKS) },
                onNavigateToReports = { navController.navigateSingleTopTo(NavRoutes.REPORTS) },
                onNavigateToUsers = { navController.navigateSingleTopTo(NavRoutes.USERS) },
                onNavigateToProfile = { navController.navigateSingleTopTo(NavRoutes.PROFILE) },
                onOpenChat = { navController.navigateSingleTopTo(NavRoutes.CHAT) },
                onLogout = onLogout
            )
        }
        composable(NavRoutes.CHAT) {
            ChatScreen(
                onBackToProjects = {
                    if (!navController.popBackStack()) {
                        navController.navigateSingleTopTo(NavRoutes.PROJECTS)
                    }
                }
            )
        }
    }
}

private fun NavHostController.navigateSingleTopTo(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
    }
}
