package com.ka.gastos.features.gastos.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ka.gastos.features.auth.data.remote.dto.User
import com.ka.gastos.features.gastos.domain.model.Expense
import com.ka.gastos.features.presentation.viewmodel.HomeViewModel

@Composable
fun GastosScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var descripcion by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        state.error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items = state.expenses, key = { it.id }) { gasto ->
                GastoItem(gasto = gasto, currentUser = state.currentUser)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it },
                label = { Text("Monto") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val montoValue = monto.toDoubleOrNull() ?: 0.0
                    if (descripcion.isNotBlank() && montoValue > 0) {
                        // Suponiendo que el grupoId es 1, idealmente debería pasarse como argumento a la pantalla
                        viewModel.addExpense(descripcion, montoValue, 1) 
                        descripcion = ""
                        monto = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear Gasto")
            }
        }
    }
}

@Composable
fun GastoItem(gasto: Expense, currentUser: User?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = gasto.descripcion, style = MaterialTheme.typography.titleMedium)
            Text(text = "Monto: ${gasto.monto}")
            val pagadorDisplay = when {
                !gasto.pagadoPor.isNullOrBlank() -> gasto.pagadoPor
                gasto.pagadorId == currentUser?.id -> currentUser.userName
                else -> "ID: ${gasto.pagadorId}"
            }
            Text(text = "Pagado por: $pagadorDisplay")
        }
    }
}
