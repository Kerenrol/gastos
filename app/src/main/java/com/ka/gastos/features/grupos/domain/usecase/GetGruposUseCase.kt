package com.ka.gastos.features.grupos.domain.usecase

import com.ka.gastos.features.grupos.domain.model.Grupo
import com.ka.gastos.features.grupos.domain.repository.GrupoRepository
import javax.inject.Inject

class GetGruposUseCase @Inject constructor(
    private val repository: GrupoRepository
) {
    suspend operator fun invoke(): List<Grupo> {
        return repository.getGrupos()
    }
}
