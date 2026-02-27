package com.ka.gastos.features.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ka.gastos.features.presentation.screens.GruposScreen
import com.ka.gastos.features.presentation.screens.HomeScreen
import com.ka.gastos.features.presentation.screens.LoginScreen
import com.ka.gastos.features.presentation.screens.RegisterScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("grupos") {
            GruposScreen(navController)
        }
        composable(
            route = "gastos/{grupoId}",
            arguments = listOf(navArgument("grupoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val grupoId = backStackEntry.arguments?.getInt("grupoId") ?: 0
            HomeScreen(navController = navController, grupoId = grupoId)
        }
    }
}
