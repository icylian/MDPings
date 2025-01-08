package com.sekusarisu.mdpings.vpings.data.networking.dto

import kotlinx.serialization.Serializable

@Serializable
data class WSServersResponsesDto(
    val now: Long = 1000000000000,
    val online: Int = 1,
    val servers: List<WSServerDto> = emptyList<WSServerDto>()
)

//@Serializable
//data class ServersResponsesDto(
//    // 提供默认值可以防止ktor在请求到没有result（kotlinx.serialization.MissingFieldException）的情况闪退
//    val result: List<ServerDto> = emptyList<ServerDto>()
//)