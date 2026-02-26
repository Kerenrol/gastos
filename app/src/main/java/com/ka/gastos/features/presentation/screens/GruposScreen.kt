package com.ka.gastos.features.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ka.gastos.features.data.model.Grupo
import com.ka.gastos.features.presentation.viewmodel.GrupoViewModel

@Composable
fun GruposScreen(navController: NavController) {
    val viewModel: GrupoViewModel = hiltViewModel()
    val grupos by viewModel.grupos.collectAsState()
    var nuevoGrupoNombre by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Mis Grupos", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = nuevoGrupoNombre,
                onValueChange = { nuevoGrupoNombre = it },
                label = { Text("Nombre del nuevo grupo") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    if (nuevoGrupoNombre.isNotBlank()) {
                        viewModel.createGrupo(nuevoGrupoNombre)
                        nuevoGrupoNombre = ""
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Crear")
            }
        }

        LazyColumn {
            items(grupos) { grupo ->
                GrupoItem(grupo = grupo) {
                    // Corregido para usar una ruta de texto simple
                    navController.navigate("home/${grupo.id}")
                }
            }
        }
    }
}

@Composable
fun GrupoItem(grupo: Grupo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = grupo.nombre,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.SemiBold
        )
    }
}
