package com.rumahtaqwa.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rumahtaqwa.ui.screens.auth.AuthScreen
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
                    val startDestination = if (viewModel.isLoggedIn) {
                        Routes.HOME
                    } else {
                        Routes.AUTH
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
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
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