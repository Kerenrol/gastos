package com.ka.gastos.features.gastos.domain.usecase

import com.ka.gastos.features.gastos.domain.repository.GastoRepository
import javax.inject.Inject

class CreateGastoUseCase @Inject constructor(
    private val repository: GastoRepository
) {
    suspend operator fun invoke(grupoId: Int, descripcion: String, monto: Double, pagadorId: Int) {
        repository.createGasto(grupoId, descripcion, monto, pagadorId)
    }
}
