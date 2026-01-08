package com.sekusarisu.mdpings.vpings.data.networking

import com.sekusarisu.mdpings.core.data.networking.constructUrl
import com.sekusarisu.mdpings.core.data.networking.constructWSUrl
import com.sekusarisu.mdpings.core.data.networking.safeCall
import com.sekusarisu.mdpings.core.domain.util.NetworkError
import com.sekusarisu.mdpings.core.domain.util.Result
import com.sekusarisu.mdpings.core.domain.util.map
import com.sekusarisu.mdpings.vpings.data.mappers.toServerGroup
import com.sekusarisu.mdpings.vpings.data.mappers.toSessionData
import com.sekusarisu.mdpings.vpings.data.mappers.toWSServer
import com.sekusarisu.mdpings.vpings.data.networking.dto.ServerGroupResponsesDto
import com.sekusarisu.mdpings.vpings.data.networking.dto.SessionResponsesDto
import com.sekusarisu.mdpings.vpings.data.networking.dto.TerminalSessionRequest
import com.sekusarisu.mdpings.vpings.data.networking.dto.WSServersResponsesDto
import com.sekusarisu.mdpings.vpings.domain.RealtimeServerDataClient
import com.sekusarisu.mdpings.vpings.domain.ServerGroup
import com.sekusarisu.mdpings.vpings.domain.SessionData
import com.sekusarisu.mdpings.vpings.domain.WSServer
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.Charset

class RemoteRealtimeServerDataClient(
    private val httpClient: HttpClient
): RealtimeServerDataClient {

    private var session: WebSocketSession? = null
    private var sshSession: WebSocketSession? = null

    fun Frame.Binary.decodeToString(charset: Charset = Charset.defaultCharset()): String {
        return try {
            // 将 Binary Frame 的数据转换为字符串
            val rawString = String(readBytes(), charset)

            // 去除 ANSI 转义序列
            rawString
//                .replace(Regex("\\x1B\\[[0-9;]*[a-zA-Z]"), "")
//                .replace(Regex("\\x1B\\[\\??[0-9;]*[a-zA-Z]"), "")
        } catch (e: Exception) {
            "解码错误: ${e.message}"
        }
    }

    override suspend fun refreshToken(baseUrl: String) {
        val url = constructUrl(
            baseURL = baseUrl,
            url = "/api/v1/refresh-token"
        )
        try {
            httpClient.get(url)
        } catch (e: Exception) {
            println("WebSocket connection error: ${e.message}")
            e.printStackTrace()
        }
    }

    override suspend fun getSession(
        baseUrl: String,
        selectedServerId: Int
    ): Result<SessionData, NetworkError> {
        val url = constructUrl(
            baseURL = baseUrl,
            url = "/api/v1/terminal"
        )
        return safeCall<SessionResponsesDto> {
            httpClient.post(
                urlString = url
            ) {
                contentType(ContentType.Application.Json)
                setBody(
                    Json.encodeToString(
                        TerminalSessionRequest("wss", selectedServerId)
                    ).also {
                        println("getSession: Request body - $it")
                    }
                )
            }
        }.map { response ->
            response.data.toSessionData()
        }
    }

    override suspend fun getServerGroup(
        baseUrl: String
    ): Result<List<ServerGroup>, NetworkError> {
        val url = constructUrl(
            baseURL = baseUrl,
            url = "/api/v1/server-group"
        )
        return safeCall<ServerGroupResponsesDto> {
            httpClient.get(
                urlString = url
            )
        }.map { response ->
            response.data.map { it.toServerGroup() }
        }
    }

    override suspend fun getServerTerminalStream(
        baseUrl: String,
        sessionId: String
    ): Flow<String> = channelFlow {
        val wsUrl = constructWSUrl(
            baseURL = baseUrl,
            url = "/api/v1/ws/terminal/${sessionId}"
        )
        try {
            // 直接初始化 sshSession
            sshSession = httpClient.webSocketSession(wsUrl)

            // 使用 sshSession 监听消息
            sshSession?.incoming?.consumeAsFlow()?.collect { frame ->
                when (frame) {
                    is Frame.Binary -> {
                        try {
                            val frameText = frame.decodeToString()
                            println("ReadText (Binary): $frameText")
                            send(frameText)
                        } catch (e: Exception) {
                            println("Error processing Binary frame: ${e.message}")
                            e.printStackTrace()
                        }
                    }
                    is Frame.Text -> {
                        val textMessage = frame.readText()
                        println("Received Text Message: $textMessage")
                        send(textMessage)
                    }
                    else -> {}
                }
            }
        } catch (e: Exception) {
            println("WebSocket connection error: ${e.message}")
            e.printStackTrace()
            // 确保在发生错误时清空 sshSession
            sshSession = null
        } finally {
            // 关闭 WebSocket 连接
            sshSession?.close()
            sshSession = null
        }
    }

    override suspend fun sendCommand(command: String) {
        try {
            // 确保 sshSession 存在且处于打开状态
            sshSession?.send(Frame.Text(command))
            println("Command sent: $command")
        } catch (e: Exception) {
            println("Error sending command: ${e.message}")
            e.printStackTrace()
        }
    }

    override suspend fun getServerListStateStream(
        baseUrl: String
    ): Flow<List<WSServer>> = channelFlow {
        val wsUrl = constructWSUrl(
            baseURL = baseUrl,
            url = "/api/v1/ws/server"
        )

        try {
            httpClient.webSocket(wsUrl) {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            try {
                                val frameText = frame.readText()
                                println("ReadText: $frameText")
                                val decoded = Json.decodeFromString<WSServersResponsesDto>(frameText)
                                val servers = decoded.servers
                                    .sortedBy { it.id }
                                    .map { it.toWSServer() }

                                send(servers)
                            } catch (e: Exception) {
                                println("Error processing WebSocket frame: ${e.message}")
                                e.printStackTrace()
                            }
                        }
                        else -> {}
                    }
                }
            }
        } catch (e: Exception) {
            println("WebSocket connection error: ${e.message}")
            e.printStackTrace()
        }
    }

    override suspend fun close() {
        session?.close(CloseReason(CloseReason.Codes.NORMAL, "User requested close"))
        session = null
    }

    override suspend fun disconnect() {
        sshSession?.close(CloseReason(CloseReason.Codes.NORMAL, "User requested close"))
        sshSession = null
    }

}