package com.sekusarisu.mdpings_v0.vpings.presentation.models

import com.sekusarisu.mdpings_v0.vpings.domain.IpAPI

data class IpAPIUi(
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

fun IpAPI.toIpAPIUi(): IpAPIUi {
    return IpAPIUi(
        query = query,
        status = status,
        country = country,
        countryCode = countryCode,
        region = region,
        regionName = regionName,
        city = city,
        zip = zip,
        lat = lat,
        lon = lon,
        timezone = timezone,
        isp = isp,
        org = org,
        `as` = `as`
    )
}