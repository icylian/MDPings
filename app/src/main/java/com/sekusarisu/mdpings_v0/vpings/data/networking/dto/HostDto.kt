package com.sekusarisu.mdpings_v0.vpings.data.networking.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class HostDto(
    @SerialName(value = "Platform") val platform: String,
    @SerialName(value = "PlatformVersion") val platformVersion: String,
    @SerialName(value = "CPU") val cpu: List<String>?,
    @SerialName(value = "MemTotal") val memTotal: Long,
    @SerialName(value = "DiskTotal") val diskTotal: Long,
    @SerialName(value = "SwapTotal") val swapTotal: Long,
    @SerialName(value = "Arch") val arch: String,
    @SerialName(value = "Virtualization") val virtualization: String,
    @SerialName(value = "BootTime") val bootTime: Int,
    @SerialName(value = "CountryCode") val countryCode: String,
    @SerialName(value = "Version") val version: String,
//    @SerializedName(value = "GPU") val gpu: Any
)