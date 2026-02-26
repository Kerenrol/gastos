package com.ka.gastos.features.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ka.gastos.features.presentation.components.CustomTextField
import com.ka.gastos.features.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(navController: NavController) {
    val viewModel: HomeViewModel = hiltViewModel()
    val grupoId = navController.currentBackStackEntry?.arguments?.getInt("grupoId") ?: 0
    
    var descripcion by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var pagadorId by remember { mutableStateOf("") }

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
                label = "Monto ($)"
            )

            CustomTextField(
                value = pagadorId,
                onValueChange = { pagadorId = it },
                label = "ID del Pagador"
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull() ?: 0.0
                    val pagadorIdInt = pagadorId.toIntOrNull() ?: 0

                    if (descripcion.isNotBlank() && montoDouble > 0 && pagadorIdInt > 0 && grupoId != 0) {
                        viewModel.addExpense(descripcion, montoDouble, pagadorIdInt, grupoId)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = descripcion.isNotBlank() && monto.isNotBlank() && pagadorId.isNotBlank()
            ) {
                Text("Guardar Gasto")
            }
        }
    }
}
