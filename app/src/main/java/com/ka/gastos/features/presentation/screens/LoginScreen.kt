package com.ka.gastos.features.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ka.gastos.features.presentation.components.CustomTextField
import com.ka.gastos.features.presentation.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state = viewModel.loginState

    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            // Navegamos a la pantalla de grupos y limpiamos el historial de navegación
            navController.navigate("grupos") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login Screen", modifier = Modifier.padding(bottom = 24.dp))

        CustomTextField(
            value = userName,
            onValueChange = { userName = it },
            label = "Username"
        )

        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            visualTransformation = PasswordVisualTransformation()
        )

        Button(onClick = { viewModel.onLogin(userName, password) }) {
            Text("Entrar")
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.loginError?.let {
            Text(text = it, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        val annotatedString = buildAnnotatedString {
            append("¿No tienes cuenta? ")
            pushStringAnnotation(tag = "REGISTER", annotation = "register")
            withStyle(style = SpanStyle(color = Color.Blue)) {
                append("Crea una aquí")
            }
            pop()
        }

        ClickableText(
            text = annotatedString,
            onClick = {
                annotatedString.getStringAnnotations(tag = "REGISTER", start = it, end = it)
                    .firstOrNull()?.let {
                        navController.navigate("register")
                    }
            }
        )
    }
}
