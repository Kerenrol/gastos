package com.ka.gastos.features.grupos.domain.repository

import com.ka.gastos.features.grupos.domain.model.Grupo

interface GrupoRepository {
    suspend fun getGrupos(): List<Grupo>
    suspend fun createGrupo(nombre: String)
}
