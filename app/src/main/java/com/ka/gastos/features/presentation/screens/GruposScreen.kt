package com.ka.gastos.features.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ka.gastos.features.grupos.domain.model.Grupo
import com.ka.gastos.features.presentation.grupos.GruposViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GruposScreen(
    navController: NavController,
    viewModel: GruposViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showCreateGroupDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getGrupos()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateGroupDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Crear Grupo")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            state.error?.let {
                Text(
                    text = "Error al cargar los grupos: $it",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.grupos) { grupo ->
                    GrupoItem(grupo = grupo, onClick = {
                        navController.navigate("gastos/${grupo.id}")
                    })
                }
            }
        }

        if (showCreateGroupDialog) {
            CreateGroupDialog(viewModel = viewModel, onDismiss = { showCreateGroupDialog = false })
        }
    }
}

@Composable
fun CreateGroupDialog(viewModel: GruposViewModel, onDismiss: () -> Unit) {
    var groupName by remember { mutableStateOf("") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear nuevo grupo") },
        text = {
            TextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Nombre del grupo") }
            )
        },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = {
                    viewModel.createGrupo(groupName)
                    onDismiss()
                }
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            androidx.compose.material3.Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
fun GrupoItem(grupo: Grupo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = grupo.nombre, style = MaterialTheme.typography.titleLarge)
        }
    }
}
