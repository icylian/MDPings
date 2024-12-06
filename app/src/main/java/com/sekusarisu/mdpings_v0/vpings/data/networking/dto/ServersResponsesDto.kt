package com.sekusarisu.mdpings_v0.vpings.data.networking.dto

import kotlinx.serialization.Serializable

@Serializable
data class ServersResponsesDto(
    // 提供默认值可以防止ktor在请求到没有result（kotlinx.serialization.MissingFieldException）的情况闪退
    val result: List<ServerDto> = emptyList<ServerDto>()
)