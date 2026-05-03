package com.example.echo.ui.navigation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.echo.ui.components.MainTab
import com.example.echo.ui.components.EchoScaffold
import com.example.echo.ui.devices.DevicesScreen
import com.example.echo.ui.filter.FilterScreen
import com.example.echo.ui.home.HomeScreen
import com.example.echo.ui.onboarding.OnboardingScreen
import com.example.echo.ui.theme.OnSurfaceVariant
import com.example.echo.ui.theme.Surface

@Composable
fun EchoNavGraph() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("echo", Context.MODE_PRIVATE) }
    val hasOnboarded = remember { mutableStateOf(prefs.getBoolean("has_onboarded", false)) }
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = if (hasOnboarded.value) MainTab.Feed.route else "onboarding"
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onGetStarted = {
                    prefs.edit().putBoolean("has_onboarded", true).apply()
                    hasOnboarded.value = true
                    navController.navigate(MainTab.Feed.route) {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable(MainTab.Feed.route) {
            HomeScreen(
                onTabSelected = { navController.navigateToTab(it) }
            )
        }
        composable(MainTab.Apps.route) {
            FilterScreen(
                onTabSelected = { navController.navigateToTab(it) }
            )
        }
        composable(MainTab.Devices.route) {
            DevicesScreen(
                onTabSelected = { navController.navigateToTab(it) }
            )
        }
        composable(MainTab.Settings.route) {
            SettingsPlaceholder(
                onTabSelected = { navController.navigateToTab(it) }
            )
        }
    }
}

private fun NavHostController.navigateToTab(tab: MainTab) {
    navigate(tab.route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(MainTab.Feed.route) {
            saveState = true
        }
    }
}

@Composable
private fun SettingsPlaceholder(onTabSelected: (MainTab) -> Unit) {
    EchoScaffold(
        selectedTab = MainTab.Settings,
        onTabSelected = onTabSelected
    ) { padding ->
        Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .background(Surface)
                .padding(padding)
                .padding(PaddingValues(24.dp)),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Settings", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Account, privacy, and sync preferences will live here.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )
        }
    }
}
