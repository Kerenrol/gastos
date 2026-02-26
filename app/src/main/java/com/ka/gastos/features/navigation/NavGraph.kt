package com.ka.gastos.features.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ka.gastos.features.presentation.screens.AddExpenseScreen
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
        composable("home") {
            HomeScreen(navController)
        }
        composable("add_expense") {
            AddExpenseScreen(navController)
        }
    }
}
