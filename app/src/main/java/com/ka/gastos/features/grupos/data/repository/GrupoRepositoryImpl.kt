package com.ka.gastos.features.grupos.data.repository

import com.ka.gastos.features.data.remote.ApiService
import com.ka.gastos.features.grupos.data.remote.dto.CreateGrupoRequest
import com.ka.gastos.features.grupos.domain.model.Grupo
import com.ka.gastos.features.grupos.domain.repository.GrupoRepository
import javax.inject.Inject

class GrupoRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : GrupoRepository {
    override suspend fun getGrupos(): List<Grupo> {
        return apiService.getGrupos()
    }

    override suspend fun createGrupo(nombre: String) {
        apiService.createGrupo(CreateGrupoRequest(nombre = nombre))
    }
}
