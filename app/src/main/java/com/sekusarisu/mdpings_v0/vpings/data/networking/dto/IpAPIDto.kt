package com.sekusarisu.mdpings_v0.vpings.data.networking.dto

import kotlinx.serialization.Serializable

@Serializable
data class IpAPIDto(
    val query: String,
    val status: String,
    val country: String,
    val countryCode: String,
    val region: String,
    val regionName: String,
    val city: String,
    val zip: String,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val isp: String,
    val org: String,
    val `as`: String
)
