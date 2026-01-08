package com.sekusarisu.mdpings.vpings.domain

import com.sekusarisu.mdpings.core.domain.util.NetworkError
import com.sekusarisu.mdpings.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface RealtimeServerDataClient {
    suspend fun refreshToken(baseUrl: String)
    suspend fun getSession(baseUrl: String, selectedServerId: Int): Result<SessionData, NetworkError>
    suspend fun getServerGroup(baseUrl: String): Result<List<ServerGroup>, NetworkError>
    suspend fun getServerListStateStream(baseUrl: String): Flow<List<WSServer>>
    suspend fun getServerTerminalStream(baseUrl: String, sessionId: String): Flow<String>
    suspend fun sendCommand(command: String)
    suspend fun close()
    suspend fun disconnect()
}