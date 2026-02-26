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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ka.gastos.features.data.model.Expense
import com.ka.gastos.features.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(navController: NavController) {
    // El ViewModel se obtiene automáticamente a través de Hilt
    val viewModel: HomeViewModel = hiltViewModel()
    val expenses by viewModel.expenses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // El grupoId se obtiene de los argumentos de la ruta
    val grupoId = navController.currentBackStackEntry?.arguments?.getInt("grupoId") ?: 0

    LaunchedEffect(key1 = grupoId) {
        if (grupoId != 0) {
            viewModel.connectAndLoad(grupoId)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_expense/$grupoId") }) {
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
                    items(expenses) { expense ->
                        ExpenseItem(expense)
                    }
                }
            }
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
