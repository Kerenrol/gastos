package com.ka.gastos.features.data.mapper

import com.ka.gastos.features.data.dto.GrupoDto
import com.ka.gastos.features.grupos.domain.model.Grupo

fun GrupoDto.toGrupo(): Grupo {
    return Grupo(
        id = id,
        nombre = nombre
    )
}
