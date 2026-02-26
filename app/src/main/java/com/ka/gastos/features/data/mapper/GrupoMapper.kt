package com.ka.gastos.features.data.mapper

import com.ka.gastos.features.data.model.Grupo
import com.ka.gastos.features.data.remote.dto.GrupoDto

fun GrupoDto.toGrupo(): Grupo {
    return Grupo(
        id = id,
        nombre = nombre
    )
}
