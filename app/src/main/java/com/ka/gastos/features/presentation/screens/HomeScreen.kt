package com.ka.gastos.features.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ka.gastos.features.data.model.Expense
import com.ka.gastos.features.presentation.components.CustomTextField
import com.ka.gastos.features.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = hiltViewModel()
    val expenses by viewModel.expenses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val grupoId = navController.currentBackStackEntry?.arguments?.getInt("grupoId") ?: 0

    // Estado para controlar la visibilidad del Bottom Sheet
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = grupoId) {
        if (grupoId != 0) {
            viewModel.connectAndLoad(grupoId)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showBottomSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Gasto")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Mis Gastos (Grupo: $grupoId)", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (error != null) {
                Text(text = error!!, color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // ---- ¡SOLUCIÓN FINAL! ----
                    // Se añade una `key` única y estable para cada elemento.
                    // Esto permite a Compose identificar qué elemento es nuevo y actualizar la UI.
                    items(items = expenses, key = { it.id }) { expense ->
                        ExpenseItem(expense)
                    }
                }
            }
        }

        // --- Bottom Sheet para añadir un gasto ---
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                AddExpenseForm(viewModel = viewModel, grupoId = grupoId) {
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddExpenseForm(viewModel: HomeViewModel, grupoId: Int, onGastoCreated: () -> Unit) {
    var descripcion by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var pagadorId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Nuevo Gasto", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        CustomTextField(value = descripcion, onValueChange = { descripcion = it }, label = "Descripción")
        CustomTextField(value = monto, onValueChange = { monto = it }, label = "Monto")
        CustomTextField(value = pagadorId, onValueChange = { pagadorId = it }, label = "ID del Pagador")
        Button(
            onClick = {
                val montoDouble = monto.toDoubleOrNull() ?: 0.0
                val pagadorIdInt = pagadorId.toIntOrNull() ?: 0
                if (descripcion.isNotBlank() && montoDouble > 0 && pagadorIdInt > 0) {
                    viewModel.createGasto(descripcion, montoDouble, pagadorIdInt, grupoId)
                    onGastoCreated()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Gasto")
        }
    }
}


@Composable
fun ExpenseItem(expense: Expense) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = expense.descripcion, fontWeight = FontWeight.Bold)
            Text(text = "Pagado por: ${expense.pagadoPor}", fontSize = 12.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "$${expense.monto}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
