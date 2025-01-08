package com.sekusarisu.mdpings.vpings.presentation.models

import com.sekusarisu.mdpings.vpings.domain.WSHost
import com.sekusarisu.mdpings.vpings.domain.WSServer
import com.sekusarisu.mdpings.vpings.domain.WSState
import com.sekusarisu.mdpings.vpings.domain.WSTemperatures

data class WSServerUi(
    val id: Int,
    val name: String,
    val displayIndex: Int,
    val host: WSHostUi,
    val status: WSStateUi,
    val countryCode: String,
    val lastActive: String,
    val isOnline: Boolean
)

data class WSHostUi(
    val platform: String,
    val platformVersion: String,
    val cpu: String,
    val core: Int,
    val memTotal: Long,
    val diskTotal: Long,
    val swapTotal: Long,
    val arch: String,
    val virtualization: String,
    val bootTime: Long,
    val version: String,
    val gpu: String
)

data class WSStateUi(
    val cpu: Float,
    val memUsed: Long,
    val diskUsed: Long,
    val swapUsed: Long,
    val load1: DoubleDisplayableNumber,
    val load5: DoubleDisplayableNumber,
    val load15: DoubleDisplayableNumber,
    val netInTransfer: LongDisplayableString,
    val netOutTransfer: LongDisplayableString,
    val netInSpeed: NetIOSpeedDisplayableString,
    val netOutSpeed: NetIOSpeedDisplayableString,
    val uptime: Long,
    val tcpConnCount: Int,
    val udpConnCount: Int,
    val processCount: Int,
    val gpu: String,
    val temperatures: List<WSTemperaturesUi>
)

data class WSTemperaturesUi(
    val name: String,
    val temperature: Float
)

fun WSServer.toWSServerUi(): WSServerUi {
    return WSServerUi(
        id = id,
        name = name,
        displayIndex = displayIndex ?: 0,
        host = host.toWSHostUi(),
        status = status.toWSStatusUi(),
        countryCode = (countryCode ?: "un").countryCodeCheck().toCountryCodeToEmojiFlag(),
        lastActive = lastActive ?: "1990-01-01T00:00:01.168169338Z",
        isOnline = (lastActive ?: "1990-01-01T00:00:01.168169338Z").toEpochMilli().toIsOnline()
    )
}

private fun WSHost.toWSHostUi(): WSHostUi {
    return WSHostUi(
        platform = platform ?: "unknown",
        platformVersion = platformVersion ?: "unknown",
        cpu = cpu?.joinToString("\n") ?: "N/A",
        core = cpu?.extractLastNumberAndSum() ?: 0,
        memTotal = memTotal ?: 0,
        diskTotal = diskTotal ?: 0,
        arch = arch ?: "unknown",
        bootTime = bootTime ?: 0,
        version = version ?: "unknown",
        swapTotal = swapTotal ?: 0,
        virtualization = virtualization ?: "unknown",
        gpu = gpu?.joinToString("\n") ?: "N/A"
    )
}

private fun WSState.toWSStatusUi(): WSStateUi {
    return WSStateUi(
        memUsed = memUsed ?: 0L,
        diskUsed = diskUsed ?: 0L,
        swapUsed = swapUsed ?: 0L,
        netInTransfer = (netInTransfer ?: 0).toNetTRLongDisplayableString(),
        netOutTransfer = (netOutTransfer ?: 0).toNetTRLongDisplayableString(),
        netInSpeed = (netInSpeed ?: 0).toNetIOSpeedDisplayableString(),
        netOutSpeed = (netOutSpeed ?: 0).toNetIOSpeedDisplayableString(),
        uptime = uptime ?: 0,
        tcpConnCount = tcpConnCount ?: 0,
        udpConnCount = udpConnCount ?: 0,
        processCount = processCount ?: 0,
        cpu = cpu ?: 0f,
        load1 = (load1 ?: 0.0).toDisplayableNumber(),
        load5 = (load5 ?: 0.0).toDisplayableNumber(),
        load15 = (load15 ?: 0.0).toDisplayableNumber(),
        gpu = gpu?.joinToString("|") ?: "N/A",
        temperatures = temperatures?.map { it.toWSTemperaturesUi() } ?: emptyList()
    )
}

private fun WSTemperatures.toWSTemperaturesUi(): WSTemperaturesUi {
    return WSTemperaturesUi(
        name = name ?: "N/A",
        temperature = temperature ?: 0F
    )
}