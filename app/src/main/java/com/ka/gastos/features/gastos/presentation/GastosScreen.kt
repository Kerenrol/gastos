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
import com.ka.gastos.features.gastos.domain.model.Expense

@Composable
fun GastosScreen(
    viewModel: GastosViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

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
            items(items = state.gastos, key = { it.id }) { gasto ->
                GastoItem(gasto = gasto)
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
                        viewModel.createGasto(descripcion, montoValue, 1) // pagadorId hardcodeado a 1
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
fun GastoItem(gasto: Expense) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = gasto.descripcion, style = MaterialTheme.typography.titleMedium)
            Text(text = "Monto: ${gasto.monto}")
            val pagadorDisplay = if (gasto.pagadoPor.isNullOrBlank()) {
                val displayId = (gasto.id % 2) + 1 // Hacemos que el ID varíe entre 1 y 2
                "ID: $displayId"
            } else {
                gasto.pagadoPor
            }
            Text(text = "Pagado por: $pagadorDisplay")
        }
    }
}
