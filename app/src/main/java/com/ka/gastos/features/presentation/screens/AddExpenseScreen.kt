package com.ka.gastos.features.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ka.gastos.features.presentation.components.CustomTextField
import com.ka.gastos.features.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var descripcion by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var pagadoPor by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Gasto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = "Descripción (ej. Cena, Gasolina)"
            )

            CustomTextField(
                value = monto,
                onValueChange = { monto = it },
                label = "Monto ($)",
                // Nota: Tu CustomTextField debería aceptar KeyboardOptions para mejorar esto
            )

            CustomTextField(
                value = pagadoPor,
                onValueChange = { pagadoPor = it },
                label = "Pagado por"
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull() ?: 0.0
                    if (descripcion.isNotBlank() && montoDouble > 0 && pagadoPor.isNotBlank()) {
                        viewModel.addExpense(descripcion, montoDouble, pagadoPor)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = descripcion.isNotBlank() && monto.isNotBlank() && pagadoPor.isNotBlank()
            ) {
                Text("Guardar Gasto")
            }
        }
    }
}
