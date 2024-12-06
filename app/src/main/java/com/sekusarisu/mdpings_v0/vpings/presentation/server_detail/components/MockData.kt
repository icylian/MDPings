package com.sekusarisu.mdpings_v0.vpings.presentation.server_detail.components

import com.sekusarisu.mdpings_v0.vpings.presentation.models.IpAPIUi
import com.sekusarisu.mdpings_v0.vpings.presentation.models.MonitorUi
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerModel
import kotlin.random.Random

// 模拟数据
public fun mockLineModel(): CartesianChartModel {
    return CartesianChartModel(
        LineCartesianLayerModel.build {
            series(x = mockEpoch, y = mockDelayList())
            series(x = mockEpoch, y = mockDelayList())
            series(x = mockEpoch, y = mockDelayList())
            series(x = mockEpoch, y = mockDelayList())
            series(x = mockEpoch, y = mockDelayList())
            series(x = mockEpoch, y = mockDelayList())
            series(x = mockEpoch, y = mockDelayList())
            series(x = mockEpoch, y = mockDelayList())
            series(x = mockEpoch, y = mockDelayList())
            series(x = mockEpoch, y = mockDelayList())
            series(x = mockEpoch, y = mockDelayList())
            series(x = mockEpoch, y = mockDelayList())
        }
    )
}

// mock monitor_name
public val monitor_name = listOf<String>(
    "重庆电信","重庆联通","重庆移动",
    "北京电信","北京联通","北京移动",
    "上海电信","上海联通","上海移动",
    "广东电信","广东联通","山东电信",
)

// mock delay
public fun mockDelayList(): List<Number> {
    return List(360) {
        Random.nextDouble(100.0, 1000.0)
    }
}

// mock time Epoch
public val mockEpoch: List<Number> = List(360) { index ->
        1730711640000 + 240000 * (index + 1)
    }

public val mockMonitors: List<MonitorUi> = List(12) { index ->
    MonitorUi(
        monitorId = index,
        serverId = 6,
        monitorName = monitor_name[index],
        serverName = "WAP.AC",
        createdAt = mockEpoch.map { it.toLong() },
        avgDelay = mockDelayList().map { it.toDouble() },
        pktLoss24h = mockPktLoss(),
        avgDelay24h = mockDelay(),
        avgDelay30mins = mockDelay(),
        pktLoss30mins = mockPktLoss(),
    )
}

fun mockPktLoss(): String {
    return "%.2f".format((Random.nextDouble() * 100)) + "%"
}

fun mockDelay(): String {
    return "%.1f".format((Random.nextDouble() * 1000)) + "ms"
}

public val mockIpAPIUi = IpAPIUi(
    query = "24.48.0.1",
    status = "success",
    country = "Canada",
    countryCode = "CA",
    region = "QC",
    regionName = "Quebec",
    city = "Montreal",
    zip = "H1L",
    lat = 45.6026,
    lon = -73.5167,
    timezone = "America/Toronto",
    isp = "Le Groupe Videotron Ltee",
    org = "Videotron Ltee",
    `as` = "AS5769 Videotron Ltee"
)