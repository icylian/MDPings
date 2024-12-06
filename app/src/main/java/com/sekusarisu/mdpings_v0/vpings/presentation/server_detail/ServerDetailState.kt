package com.sekusarisu.mdpings_v0.vpings.presentation.server_detail

import androidx.compose.runtime.Immutable
import com.sekusarisu.mdpings_v0.vpings.presentation.models.IpAPIUi
import com.sekusarisu.mdpings_v0.vpings.presentation.models.MonitorUi
import com.sekusarisu.mdpings_v0.vpings.presentation.models.ServerUi

@Immutable
data class ServerDetailState(
    val isLoading: Boolean = false,
    val isChartLoading: Boolean = false,
    val serverUi: ServerUi? = null,
    val ipAPIUi: IpAPIUi? = null,
    val monitors: List<MonitorUi> = emptyList(),
    val monitorsOrigin: List<MonitorUi> = emptyList(),
    val monitorsTimeSlice: String = ""
)
