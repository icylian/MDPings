package com.sekusarisu.mdpings_v0.vpings.data.networking.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MonitorDto(
    @SerialName(value = "monitor_id") val monitorId: Int,
    @SerialName(value = "server_id") val serverId: Int,
    @SerialName(value = "monitor_name") val monitorName: String,
    @SerialName(value = "server_name") val serverName: String,
    @SerialName(value = "created_at") val createdAt: List<Long>,
    @SerialName(value = "avg_delay") val avgDelay: List<Double>
)