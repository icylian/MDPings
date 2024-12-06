package com.sekusarisu.mdpings_v0.vpings.presentation.server_detail

import com.sekusarisu.mdpings_v0.vpings.presentation.models.ServerUi

interface ServerDetailAction {
    data class OnLoadSingleServer(val serverUi: ServerUi, val apiURL: String, val apiTOKEN: String, val interval: Int): ServerDetailAction
    data class OnLoadInfoAndMonitors(val serverUi: ServerUi, val monitorsTimeSlice: String, val apiURL: String, val apiTOKEN: String, val interval: Int): ServerDetailAction
    data class OnMonitorsRefresh(val serverId: Int, val monitorsTimeSlice: String, val apiURL: String, val apiTOKEN: String): ServerDetailAction
    data class OnSliceMonitorsTime(val time: String): ServerDetailAction
    object OnDisposeCleanUp: ServerDetailAction
}