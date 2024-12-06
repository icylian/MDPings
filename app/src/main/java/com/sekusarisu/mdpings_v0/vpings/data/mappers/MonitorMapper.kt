package com.sekusarisu.mdpings_v0.vpings.data.mappers

import com.sekusarisu.mdpings_v0.vpings.data.networking.dto.MonitorDto
import com.sekusarisu.mdpings_v0.vpings.domain.Monitor

fun MonitorDto.toMonitor(): Monitor {
    return Monitor(
        monitorId = monitorId,
        serverId = serverId,
        monitorName = monitorName,
        serverName = serverName,
        createdAt = createdAt,
        avgDelay = avgDelay
    )
}