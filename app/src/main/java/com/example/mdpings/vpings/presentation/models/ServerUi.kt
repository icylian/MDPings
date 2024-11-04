package com.example.mdpings.vpings.presentation.models

import android.icu.text.NumberFormat
import com.example.mdpings.vpings.domain.Host
import com.example.mdpings.vpings.domain.Server
import com.example.mdpings.vpings.domain.Status
import java.util.Locale
import kotlin.math.pow

data class ServerUi(
    val id: Int,
    val name: String,
    val tag: String,
    val lastActive: Int,
    val ipv4: String,
    val ipv6: String,
    val validIp: String,
    val displayIndex: Int,
    val hideForGuest: Boolean,
    val host: HostUi,
    val status: StatusUi
)

data class HostUi(
    val platform: String,
    val platformVersion: String,
    val cpu: List<String>,
    val memTotal: Long,
    val diskTotal: Long,
    val swapTotal: Int,
    val arch: String,
    val virtualization: String,
    val bootTime: Int,
    val countryCode: String,
    val version: String,
//    val GPU: Any
)

data class StatusUi(
    val cpu: Double,
    val memUsed: Long,
    val swapUsed: Int,
    val diskUsed: Long,
    val netInTransfer: LongDisplayableString,
    val netOutTransfer: LongDisplayableString,
    val netInSpeed: NetIOSpeedDisplayableString,
    val netOutSpeed: NetIOSpeedDisplayableString,
    val uptime: Int,
    val load1: DoubleDisplayableNumber,
    val load5: DoubleDisplayableNumber,
    val load15: DoubleDisplayableNumber,
    val tcpConnCount: Int,
    val udpConnCount: Int,
    val processCount: Int,
//    val Temperatures: Any,
    val gpu: Int
)

data class DoubleDisplayableNumber(
    val value: Double,
    val formatted: String
)

fun Double.toDisplayableNumber(): DoubleDisplayableNumber {
    val formatter = NumberFormat.getNumberInstance(java.util.Locale.getDefault()).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return DoubleDisplayableNumber(
        value = this,
        formatted = formatter.format(this)
    )
}

data class NetIOSpeedDisplayableString(
    val value: Int,
    val formatted: String
)

fun Int.toNetIOSpeedDisplayableString(): NetIOSpeedDisplayableString {
    val speed = when {
        this / 1024 < 1024 -> "${String.format(Locale.US, "%.2f", this / 1024.0)} K/s"
        this / 1024 / 1024 < 1024 -> "${String.format(Locale.US, "%.2f", this / 1024.0.pow(2.0))} M/s"
        else -> "${String.format(Locale.US, "%.2f", this / 1024.0.pow(3.0))} G/s"
    }
    return NetIOSpeedDisplayableString(
        value = this,
        formatted = speed
    )
}

data class LongDisplayableString(
    val value: Long,
    val formatted: String
)

fun Long.toLongDisplayableString(): LongDisplayableString {
    val transfer = when {
        this / 1024 < 1024 -> "${String.format(Locale.US, "%.2f", this / 1024.0)} K"
        this / 1024.0.pow(2.0) < 1024 -> "${String.format(Locale.US, "%.2f", this / 1024.0.pow(2.0))} M"
        this / 1024.0.pow(3.0) < 1024 -> "${String.format(Locale.US, "%.2f", this / 1024.0.pow(3.0))} G"
        this / 1024.0.pow(4.0) < 1024 -> "${String.format(Locale.US, "%.2f", this / 1024.0.pow(4.0))} T"
        else -> "${String.format(Locale.US, "%.2f", this / 1024.0.pow(5.0))} P"
    }
    return LongDisplayableString(
        value = this,
        formatted = transfer
    )
}

fun Server.toServerUi(): ServerUi {
    return ServerUi(
        id = id,
        name = name,
        tag = tag,
        lastActive = lastActive,
        ipv4 = ipv4,
        ipv6 = ipv6,
        validIp = validIp,
        displayIndex = displayIndex,
        hideForGuest = hideForGuest,
        host = host.toHostUi(),
        status = status.toStatusUi()
    )
}

private fun Host.toHostUi(): HostUi {
    return HostUi(
        platform = platform,
        platformVersion = platformVersion,
        cpu = cpu,
        memTotal = memTotal,
        diskTotal = diskTotal,
        swapTotal = swapTotal,
        arch = arch,
        virtualization = virtualization,
        bootTime = bootTime,
        countryCode = countryCode.toCountryCodeToEmojiFlag(),
        version = version
    )
}

private fun Status.toStatusUi(): StatusUi {
    return StatusUi(
        cpu = cpu,
        memUsed = memUsed,
        swapUsed = swapUsed,
        diskUsed = diskUsed,
        netInTransfer = netInTransfer.toLongDisplayableString(),
        netOutTransfer = netOutTransfer.toLongDisplayableString(),
        netInSpeed = netInSpeed.toNetIOSpeedDisplayableString(),
        netOutSpeed = netOutSpeed.toNetIOSpeedDisplayableString(),
        uptime = uptime,
        load1 = load1.toDisplayableNumber(),
        load5 = load5.toDisplayableNumber(),
        load15 = load15.toDisplayableNumber(),
        tcpConnCount = tcpConnCount,
        udpConnCount = udpConnCount,
        processCount = processCount,
        gpu = gpu
    )
}

fun String.toCountryCodeToEmojiFlag(): String {
    return this
        .uppercase(Locale.US)
        .map { char ->
            Character.codePointAt("$char", 0) - 0x41 + 0x1F1E6
        }
        .map { codePoint ->
            Character.toChars(codePoint)
        }
        .joinToString(separator = "") { charArray ->
            String(charArray)
        }
}

