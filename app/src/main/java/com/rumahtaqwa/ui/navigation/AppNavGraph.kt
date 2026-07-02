package com.rumahtaqwa.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rumahtaqwa.ui.screens.auth.AuthScreen
import com.rumahtaqwa.ui.screens.auth.verify.VerifyEmailScreen
import com.rumahtaqwa.ui.screens.splash.SplashScreen

@Composable
fun AppNavGraph(
    viewModel: NavViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(
            route = Routes.SPLASH,
            exitTransition = {
                fadeOut(animationSpec = tween(400))
            }
        ) {
            SplashScreen(
                onFinished = {
                    val startDestination = when {
                        !viewModel.isLoggedIn -> Routes.AUTH
                        viewModel.isEmailVerified() -> Routes.HOME
                        else -> Routes.VERIFY_EMAIL
                    }
                    navController.navigate(startDestination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.AUTH) {
            AuthScreen(
                onAuthSuccess = {
                    val destination = if (viewModel.isEmailVerified()) {
                        Routes.HOME
                    } else {
                        Routes.VERIFY_EMAIL
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.VERIFY_EMAIL) {
            VerifyEmailScreen(
                onVerified = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.VERIFY_EMAIL) { inclusive = true }
                    }
                },
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            MainNavigation(
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Routes.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

    }
}