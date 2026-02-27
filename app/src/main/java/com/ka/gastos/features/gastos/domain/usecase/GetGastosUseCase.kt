package com.ka.gastos.features.gastos.domain.usecase

import com.ka.gastos.features.gastos.domain.model.Expense
import com.ka.gastos.features.gastos.domain.repository.GastoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGastosUseCase @Inject constructor(
    private val repository: GastoRepository
) {
    operator fun invoke(grupoId: Int): Flow<List<Expense>> {
        return repository.getGastos(grupoId)
    }
}
