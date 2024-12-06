package com.sekusarisu.mdpings_v0.vpings.data.networking

import com.sekusarisu.mdpings_v0.core.data.networking.constructUrl
import com.sekusarisu.mdpings_v0.core.data.networking.safeCall
import com.sekusarisu.mdpings_v0.core.domain.util.NetworkError
import com.sekusarisu.mdpings_v0.core.domain.util.Result
import com.sekusarisu.mdpings_v0.core.domain.util.map
import com.sekusarisu.mdpings_v0.vpings.data.mappers.toIpAPI
import com.sekusarisu.mdpings_v0.vpings.data.mappers.toMonitor
import com.sekusarisu.mdpings_v0.vpings.data.mappers.toServer
import com.sekusarisu.mdpings_v0.vpings.data.networking.dto.IpAPIDto
import com.sekusarisu.mdpings_v0.vpings.data.networking.dto.MonitorsResponsesDto
import com.sekusarisu.mdpings_v0.vpings.data.networking.dto.ServersResponsesDto
import com.sekusarisu.mdpings_v0.vpings.domain.IpAPI
import com.sekusarisu.mdpings_v0.vpings.domain.Monitor
import com.sekusarisu.mdpings_v0.vpings.domain.Server
import com.sekusarisu.mdpings_v0.vpings.domain.ServerDataSource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header

class RemoteServerDataSource(
    private val httpClient: HttpClient
): ServerDataSource {

    override suspend fun getServers(apiUrl: String, token: String): Result<List<Server>, NetworkError> {
        return safeCall<ServersResponsesDto> {
            httpClient.get(
                urlString = constructUrl(
                    baseURL = apiUrl,
                    url = "/api/v1/server/details"
                )
            ) {
                header("Authorization", token)
            }
        }.map { response ->
            response.result
                .sortedBy { it.id }
                .map { it.toServer() }
        }
    }

    override suspend fun getSingleServer(apiUrl: String, token: String, serverId: String): Result<Server, NetworkError> {
        return safeCall<ServersResponsesDto> {
            httpClient.get(
                urlString = constructUrl(
                    baseURL = apiUrl,
                    url = "/api/v1/server/details?id=${serverId}"
                )
            ) {
                header("Authorization", token)
            }
        }.map { response ->
            response.result.first().toServer()
        }
    }

    override suspend fun getMonitors(apiUrl: String, token: String, serverId: Int): Result<List<Monitor>, NetworkError> {
        return safeCall<MonitorsResponsesDto> {
            httpClient.get(
                urlString = constructUrl(
                    baseURL = apiUrl,
                    url = "/api/v1/monitor/$serverId"
                )
            ) {
                header("Authorization", token)
            }
        }.map { response ->
            response.result
                ?.sortedBy { it.monitorId }
                ?.map { it.toMonitor() } ?: emptyList()
        }
    }

    override suspend fun getIpAPI(serverIp: String): Result<IpAPI, NetworkError> {
        return safeCall<IpAPIDto> {
            httpClient.get(
                urlString = constructUrl(
                    baseURL = "http://ip-api.com/json/",
                    url = "/${serverIp}"
                )
            )
        }.map { response ->
            response.toIpAPI()
        }
    }

}