package com.ka.gastos.features.navigation.screens

sealed class Screen(val route: String) {
    object LoginScreen : Screen("login_screen")
    object RegisterScreen : Screen("register_screen")
    object GruposScreen : Screen("grupos_screen")
    object GastosScreen : Screen("gastos_screen")
}
