package com.rumahtaqwa.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rumahtaqwa.R
import com.rumahtaqwa.ui.components.snackbar.AppSnackbarHost
import com.rumahtaqwa.ui.screens.main.home.HomeScreen
import com.rumahtaqwa.ui.screens.main.ibadah.IbadahScreen
import com.rumahtaqwa.ui.screens.main.rekap.RekapScreen
import com.rumahtaqwa.ui.screens.main.settings.SettingsScreen

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: Int
) {
    data object Home : BottomNavItem(
        route = Routes.HOME,
        label = "Beranda",
        icon = R.drawable.ic_home
//        icon = Icons.Default.Flag
    )
    data object Ibadah : BottomNavItem(
        route = Routes.IBADAH,
        label = "Ibadah",
        icon = R.drawable.ic_mosque
    )
    data object Rekap : BottomNavItem(
        route = Routes.REKAP,
        label = "Rekap",
        icon = R.drawable.ic_document
    )
    data object Settings : BottomNavItem(
        route = Routes.SETTINGS,
        label = "Pengaturan",
        icon = R.drawable.ic_settings
    )
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainNavigation(
    onLogout: () -> Unit
){
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Ibadah,
        BottomNavItem.Rekap,
        BottomNavItem.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier.navigationBarsPadding().height(55.dp)
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Column (
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(top = 5.dp)
                                ) {

                                    Icon(
                                        painter = painterResource(item.icon),
                                        contentDescription = null
                                    )

                                    Text(
                                        item.label,
                                        fontSize = 12.sp,
                                        style = MaterialTheme.typography.titleSmall,
                                    )
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = Color.Transparent,
                            )
                        )
                    }
                }
            }
        ) { _ ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
//            modifier = Modifier.padding(padding)
            ) {

                composable(Routes.HOME) {
                    HomeScreen()
                }

                composable(Routes.IBADAH) {
                    IbadahScreen()
                }

                composable(Routes.REKAP) {
                    RekapScreen()
                }

                composable(Routes.SETTINGS) {
                    SettingsScreen(
                        onLogout = onLogout
                    )
                }

            }

        }

        AppSnackbarHost(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
        )
    }

}