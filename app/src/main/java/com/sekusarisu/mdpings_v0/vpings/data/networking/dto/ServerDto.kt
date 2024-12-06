package com.sekusarisu.mdpings_v0.vpings.data.networking.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ServerDto(
    val id: Int,
    val name: String,
    val tag: String,
    @SerialName(value = "last_active") val lastActive: Long,
    val ipv4: String,
    val ipv6: String,
    @SerialName(value = "valid_ip") val validIp: String,
    @SerialName(value = "display_index") val displayIndex: Int,
    @SerialName(value = "hide_for_guest") val hideForGuest: Boolean,
    val host: HostDto,
    val status: StatusDto
)
