package com.sekusarisu.mdpings_v0.vpings.presentation.models

import com.sekusarisu.mdpings_v0.vpings.domain.Monitor

data class MonitorUi(
    val monitorId: Int,
    val serverId: Int,
    val monitorName: String,
    val serverName: String,
    val createdAt: List<Long>,
    val avgDelay: List<Double>,
    val pktLoss24h: String,
    val avgDelay24h: String,
//    val avgDelay6h: String,
//    val avgDelay3h: String,
//    val avgDelay1h: String,
    val pktLoss30mins: String,
    val avgDelay30mins: String
)

fun Monitor.toMonitorUi(): MonitorUi {
    // 计算丢包率
    val timeoutCount = avgDelay.count { it == 1000.0 }
    val pktLoss24h = "%.2f".format(timeoutCount.toDouble() / avgDelay.size * 100) + "%"

    // 过滤超时数据并将时间戳和延迟配对
    val filtered = createdAt.zip(avgDelay)
        .filter { (_, delay) -> delay != 1000.0 }
        .takeIf { it.isNotEmpty() } // 确保有数据

    // 如果没有有效数据，返回所有统计数据为N/A的对象
    if (filtered == null) {
        return MonitorUi(
            monitorId = monitorId,
            serverId = serverId,
            monitorName = monitorName,
            serverName = serverName,
            createdAt = emptyList(),
            avgDelay = emptyList(),
            pktLoss24h = pktLoss24h,
            avgDelay24h = "N/A",
            avgDelay30mins = "N/A",
            pktLoss30mins = "N/A"
        )
    }

    val currentTime = System.currentTimeMillis()

    // 计算不同时间段的平均延迟
    fun calculateAverageDelay(timeMillis: Long): String {
        return filtered
            .filter { (timestamp, _) -> timestamp >= currentTime - timeMillis }
            .map { it.second }
            .takeIf { it.isNotEmpty() }
            ?.average()
            ?.let { "%.1f".format(it) + "ms" }
            ?: "N/A"
    }

    fun calculatePktLoss30mins(timeMillis: Long): String {
        val slice = createdAt.zip(avgDelay)
            .filter { (timestamp, _) -> timestamp >= currentTime - timeMillis }
            .map { it.second }
        val timeoutCount = slice
            .takeIf { it.isNotEmpty() }
            ?.count { it == 1000.0 }
            ?: 0
        return "%.2f".format(timeoutCount.toDouble() / slice.size) + "%"
    }

    return MonitorUi(
        monitorId = monitorId,
        serverId = serverId,
        monitorName = monitorName,
        serverName = serverName,
        createdAt = filtered.map { it.first },
        avgDelay = filtered.map { it.second },
        pktLoss24h = pktLoss24h,
        pktLoss30mins = calculatePktLoss30mins(TimeConstants.MINS_30),
        avgDelay24h = calculateAverageDelay(TimeConstants.HOURS_24),
        avgDelay30mins = calculateAverageDelay(TimeConstants.MINS_30),
    )
}

object TimeConstants {
    const val MINS_30 = 30 * 60 * 1000L
    const val HOURS_1 = 60 * 60 * 1000L
    const val HOURS_3 = 3 * 60 * 60 * 1000L
    const val HOURS_6 = 6 * 60 * 60 * 1000L
    const val HOURS_24 = 24 * 60 * 60 * 1000L
}




















