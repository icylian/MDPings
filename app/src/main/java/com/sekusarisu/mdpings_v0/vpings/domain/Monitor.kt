package com.sekusarisu.mdpings_v0.vpings.domain

data class Monitor(
    val monitorId: Int,
    val serverId: Int,
    val monitorName: String,
    val serverName: String,
    val createdAt: List<Long>,
    val avgDelay: List<Double>
)