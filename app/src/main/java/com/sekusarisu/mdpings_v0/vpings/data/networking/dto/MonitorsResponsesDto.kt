package com.sekusarisu.mdpings_v0.vpings.data.networking.dto

import kotlinx.serialization.Serializable

@Serializable
data class MonitorsResponsesDto(
    val result: List<MonitorDto>? = emptyList<MonitorDto>()
)
