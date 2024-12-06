package com.sekusarisu.mdpings_v0.vpings.data.networking.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusDto(
    @SerialName(value = "CPU") val cpu: Double,
    @SerialName(value = "MemUsed") val memUsed: Long,
    @SerialName(value = "SwapUsed") val swapUsed: Long,
    @SerialName(value = "DiskUsed") val diskUsed: Long,
    @SerialName(value = "NetInTransfer") val netInTransfer: Long,
    @SerialName(value = "NetOutTransfer") val netOutTransfer: Long,
    @SerialName(value = "NetInSpeed") val netInSpeed: Long,
    @SerialName(value = "NetOutSpeed") val netOutSpeed: Long,
    @SerialName(value = "Uptime") val uptime: Long,
    @SerialName(value = "Load1") val load1: Double,
    @SerialName(value = "Load5") val load5: Double,
    @SerialName(value = "Load15") val load15: Double,
    @SerialName(value = "TcpConnCount") val tcpConnCount: Int,
    @SerialName(value = "UdpConnCount") val udpConnCount: Int,
    @SerialName(value = "ProcessCount") val processCount: Int,
//    @SerializedName(value = "Temperatures") val temperatures: Any,
    @SerialName(value = "GPU") val gpu: Int
)