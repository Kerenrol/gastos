package com.ka.gastos.features.grupos.domain.usecase

import com.ka.gastos.features.grupos.domain.repository.GrupoRepository
import javax.inject.Inject

class CreateGrupoUseCase @Inject constructor(
    private val repository: GrupoRepository
) {
    suspend operator fun invoke(nombre: String) {
        repository.createGrupo(nombre)
    }
}
